package com.example.SevMerge.withdrawal;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WithdrawalService — 전문가 출금 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    /** 출금 신청 — 잔액 차감 + Withdrawal 저장 */
    @Transactional
    public void requestWithdrawal(Long memberId,
                                  String bankName,
                                  String accountNumber,
                                  String accountHolder,
                                  Integer amount) {

        if (amount == null || amount < 10000) {
            throw new BadRequestException("최소 출금 금액은 10,000원입니다.");
        }

        // 현재 잔액 확인
        Integer currentBalance = (Integer) em
                .createQuery("SELECT m.balance FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();

        if (currentBalance == null || currentBalance < amount) {
            throw new BadRequestException("잔액이 부족합니다. 현재 잔액: " + (currentBalance != null ? currentBalance : 0) + "원");
        }

        // 잔액 차감
        em.createQuery("UPDATE Member m SET m.balance = m.balance - :amount WHERE m.id = :id")
                .setParameter("amount", amount)
                .setParameter("id", memberId)
                .executeUpdate();

        // 출금 기록 저장
        Withdrawal withdrawal = Withdrawal.builder()
                .memberId(memberId)
                .amount(amount)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolder(accountHolder)
                .status(WithdrawalStatus.PENDING)
                .build();
        withdrawalRepository.save(withdrawal);

        log.info("[Withdrawal] 출금 신청 — memberId={}, amount={}, bank={}", memberId, amount, bankName);
    }

    /** 출금내역 조회 */
    public HistoryResult getHistory(Long memberId) {
        List<Withdrawal> raw = withdrawalRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        List<WithdrawalDTO> dtos = raw.stream().map(w -> new WithdrawalDTO(
                w.getId(),
                w.getBankName(),
                maskAccount(w.getAccountNumber()),
                w.getAccountHolder(),
                formatAmount(w.getAmount()),
                sdf.format(new java.util.Date(w.getCreatedAt().getTime())),
                w.getStatus() == WithdrawalStatus.PENDING,
                w.getStatus() == WithdrawalStatus.COMPLETED,
                w.getStatus() == WithdrawalStatus.REJECTED
        )).toList();

        int totalCount  = dtos.size();
        int totalAmount = raw.stream()
                .filter(w -> w.getStatus() == WithdrawalStatus.COMPLETED || w.getStatus() == WithdrawalStatus.PENDING)
                .mapToInt(Withdrawal::getAmount).sum();

        return new HistoryResult(dtos, totalCount, formatAmount(totalAmount));
    }

    // ── 내부 유틸 ──

    private String maskAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return accountNumber;
        return accountNumber.substring(0, accountNumber.length() - 4).replaceAll(".", "*")
                + accountNumber.substring(accountNumber.length() - 4);
    }

    private String formatAmount(int amount) {
        return String.format("%,d", amount);
    }

    /** 관리자 — 전체 출금 요청 조회 */
    public List<AdminWithdrawalDTO> getAllForAdmin(String status) {
        List<Withdrawal> list;
        if ("PENDING".equals(status)) {
            list = withdrawalRepository.findAllByStatusOrderByCreatedAtDesc(WithdrawalStatus.PENDING);
        } else if ("COMPLETED".equals(status)) {
            list = withdrawalRepository.findAllByStatusOrderByCreatedAtDesc(WithdrawalStatus.COMPLETED);
        } else if ("REJECTED".equals(status)) {
            list = withdrawalRepository.findAllByStatusOrderByCreatedAtDesc(WithdrawalStatus.REJECTED);
        } else {
            list = withdrawalRepository.findAllByOrderByCreatedAtDesc();
        }

        List<Long> memberIds = list.stream().map(Withdrawal::getMemberId).distinct().collect(Collectors.toList());
        Map<Long, Member> memberMap = memberRepository.findAllById(memberIds)
                .stream().collect(Collectors.toMap(Member::getId, m -> m));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return list.stream().map(w -> {
            Member m = memberMap.get(w.getMemberId());
            String name  = m != null ? m.getName()  : "(알 수 없음)";
            String email = m != null ? m.getEmail() : "";
            String statusLabel = switch (w.getStatus()) {
                case PENDING   -> "처리중";
                case COMPLETED -> "완료";
                case REJECTED  -> "반려";
            };
            return new AdminWithdrawalDTO(
                    w.getId(), name, email,
                    formatAmount(w.getAmount()),
                    w.getBankName(), maskAccount(w.getAccountNumber()), w.getAccountHolder(),
                    sdf.format(new java.util.Date(w.getCreatedAt().getTime())),
                    statusLabel,
                    w.getStatus() == WithdrawalStatus.PENDING,
                    w.getStatus() == WithdrawalStatus.COMPLETED,
                    w.getStatus() == WithdrawalStatus.REJECTED
            );
        }).collect(Collectors.toList());
    }

    /** 관리자 — 출금 승인/반려 처리 */
    @Transactional
    public void processWithdrawal(Long withdrawalId, String action) {
        Withdrawal w = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new BadRequestException("출금 요청을 찾을 수 없습니다."));

        if (w.getStatus() != WithdrawalStatus.PENDING) {
            throw new BadRequestException("이미 처리된 요청입니다.");
        }

        if ("APPROVE".equals(action)) {
            w.changeStatus(WithdrawalStatus.COMPLETED);
        } else if ("REJECT".equals(action)) {
            w.changeStatus(WithdrawalStatus.REJECTED);
            // 반려 시 잔액 환불
            em.createQuery("UPDATE Member m SET m.balance = m.balance + :amount WHERE m.id = :id")
                    .setParameter("amount", w.getAmount())
                    .setParameter("id", w.getMemberId())
                    .executeUpdate();
        } else {
            throw new BadRequestException("잘못된 액션입니다.");
        }
    }

    // ── DTO / Result 레코드 ──

    public record AdminWithdrawalDTO(
            Long    id,
            String  name,
            String  email,
            String  amount,
            String  bankName,
            String  accountNumber,
            String  accountHolder,
            String  requestDate,
            String  statusLabel,
            boolean isPending,
            boolean isCompleted,
            boolean isRejected
    ) {}

    public record WithdrawalDTO(
            Long    id,
            String  bankName,
            String  accountNumber,
            String  accountHolder,
            String  amount,
            String  createdAt,
            boolean isPending,
            boolean isCompleted,
            boolean isRejected
    ) {}

    public record HistoryResult(
            List<WithdrawalDTO> withdrawals,
            int    totalCount,
            String totalAmount
    ) {}
}
