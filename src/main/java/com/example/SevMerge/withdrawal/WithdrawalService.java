package com.example.SevMerge.withdrawal;

import com.example.SevMerge.core.exception.BadRequestException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * WithdrawalService — 전문가 출금 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;

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

    // ── DTO / Result 레코드 ──

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
