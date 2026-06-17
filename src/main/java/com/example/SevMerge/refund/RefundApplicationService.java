package com.example.SevMerge.refund;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.notification.NotificationService;
import com.example.SevMerge.payment.Payment;
import com.example.SevMerge.payment.PaymentRepository;
import com.example.SevMerge.payment.PaymentService;
import com.example.SevMerge.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 환불 신청 심사
 *
 * 의뢰인의 환불 신청 접수
 * 관리자의 승인/거절 처리
 * 승인 시 PaymentService.refund() 호출
 * 처리 결과를 전문가랑 의뢰인에게 알림 발송
 *
 *
 *  Payment, Member 엔티티는 ID로만 참조해 충돌 방지
 *  여기서 직접 수정하지 않음
 *  실제 환불 실행은 PaymentService에서 실행해 중복 없음
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundApplicationService {

    private final RefundApplicationRepository refundApplicationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    // 환불 신청
    @Transactional
    public RefundApplicationResponse apply(Long clientId, Long paymentId, String reasonCategory, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));

        if (!payment.getClientId().equals(clientId)) {
            throw new ForbiddenException("본인의 결제 건만 환불 신청할 수 있습니다.");
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BadRequestException("환불 신청이 가능한 상태가 아닙니다.");
        }

        if (refundApplicationRepository.existsByPaymentIdAndStatus(paymentId, RefundApplicationStatus.PENDING)) {
            throw new BadRequestException("이미 처리 대기 중인 환불 신청이 있습니다.");
        }

        if (reason == null || reason.isBlank()) {
            throw new BadRequestException("환불 사유를 입력해 주세요.");
        }

        RefundApplication application = RefundApplication.builder()
                .paymentId(paymentId)
                .clientId(clientId)
                .expertId(payment.getExpertId())
                .reasonCategory(reasonCategory)
                .reason(reason)
                .status(RefundApplicationStatus.PENDING)
                .build();

        refundApplicationRepository.save(application);
        log.info("[RefundApplication] 신청 완료 - paymentId={}, clientId={}", paymentId, clientId);

        return toResponse(application);
    }

    // 관리자 승인 시
    @Transactional
    public RefundApplicationResponse approve(Long applicationId, String adminComment) {
        RefundApplication application = findById(applicationId);

        application.approve(adminComment);

        // 실제 환불 실행은 PaymentService에 위임
        paymentService.refund(application.getPaymentId(), application.getClientId());

        // 전문가에게 알림
        Member expert = memberRepository.findById(application.getExpertId())
                .orElseThrow(() -> new NotFoundException("전문가 정보를 찾을 수 없습니다."));
        notificationService.notify(
                expert,
                com.example.SevMerge.notification.NotificationType.REFUND_APPROVED,
                "진행 중이던 프로젝트의 환불 신청이 승인되어 계약이 취소되었습니다.",
                "/payment/my"
        );

        log.info("[RefundApplication] 승인 완료 - applicationId={}", applicationId);
        return toResponse(application);
    }

    //  관리자 거절 시
    @Transactional
    public RefundApplicationResponse reject(Long applicationId, String adminComment) {
        RefundApplication application = findById(applicationId);

        application.reject(adminComment);

        // 의뢰인에게 거절 알림
        Member client = memberRepository.findById(application.getClientId())
                .orElseThrow(() -> new NotFoundException("의뢰인 정보를 찾을 수 없습니다."));
        notificationService.notify(
                client,
                com.example.SevMerge.notification.NotificationType.REFUND_REJECTED,
                "환불 신청이 거절되었습니다. 사유: " + (adminComment != null ? adminComment : "관리자 문의"),
                "/my-pages?tab=refundHistory"
        );

        log.info("[RefundApplication] 거절 처리 - applicationId={}", applicationId);
        return toResponse(application);
    }

    //  조회 관련
    public List<RefundApplicationResponse> getMyApplications(Long clientId) {
        return refundApplicationRepository.findAllByClientIdOrderByCreatedAtDesc(clientId)
                .stream().map(this::toResponse).toList();
    }

    public List<RefundApplicationResponse> getPendingApplications() {
        return refundApplicationRepository.findAllByStatusOrderByCreatedAtAsc(RefundApplicationStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    public List<RefundApplicationResponse> getAllApplications() {
        return refundApplicationRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    //  private
    private RefundApplication findById(Long id) {
        return refundApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("환불 신청 정보를 찾을 수 없습니다."));
    }

    private RefundApplicationResponse toResponse(RefundApplication application) {
        String clientName = memberRepository.findById(application.getClientId())
                .map(Member::getName).orElse("알 수 없음");
        String expertName = memberRepository.findById(application.getExpertId())
                .map(Member::getName).orElse("알 수 없음");
        return new RefundApplicationResponse(application, clientName, expertName);
    }
}