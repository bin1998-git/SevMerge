//package com.example.SevMerge.payment;
//
//import com.example.SevMerge.core.exception.BadRequestException;
//import com.example.SevMerge.core.exception.ForbiddenException;
//import com.example.SevMerge.core.exception.NotFoundException;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Objects;
//
///**
// * PaymentService
// * 요구사항: PAY-001 ~ PAY-006
// *
// * [충돌 방지 전략]
// * - ProjectService/BidService 등 타 도메인 Service를 주입하지 않습니다.
// * - 프로젝트 상태 변경(PAY-003)은 EntityManager 네이티브 쿼리로 직접 처리합니다.
// *   → 팀장이 Project Entity를 완성해도 이 파일에 영향 없음.
// * - Notification 생성은 보조E 담당이므로 TODO 주석으로 연동 위치만 표시합니다.
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class PaymentService {
//
//    // 플랫폼 수수료율 10%
//    private static final double PLATFORM_FEE_RATE = 0.10;
//
//    private final PaymentRepository paymentRepository;
//
//    @PersistenceContext
//    private EntityManager em;
//
//    // ===================== PAY-001 + PAY-003: 결제 요청 및 완료 처리 =====================
//
//    /**
//     * merchantUid 생성 (결제 페이지 진입 시 호출)
//     * 형식: "sev-project-{projectId}"
//     * - 포트원 SDK에 merchant_uid로 전달됩니다.
//     */
//    public String generateMerchantUid(Long projectId) {
//        return "sev-project-" + projectId;
//    }
//
//    /**
//     * PAY-001, PAY-003: 포트원 결제 완료 처리
//     * 포트원 SDK 결제 후 프론트가 imp_uid를 서버로 전달하면 실행됩니다.
//     *
//     * @param clientId 세션에서 추출한 의뢰인 ID
//     * @param req      포트원 결제 완료 확인 DTO
//     */
//    @Transactional
//    public PaymentResponse completePayment(Long clientId, PaymentRequest.ConfirmRequest req) {
//
//        // 1. merchantUid 파싱 → projectId 추출
//        Long projectId = parseProjectId(req.getMerchantUid());
//
//        // 2. 중복 결제 방지 (프로젝트당 1건 제한)
//        if (paymentRepository.existsByProjectId(projectId)) {
//            throw new BadRequestException("이미 결제가 완료된 프로젝트입니다.");
//        }
//
//        // 3. TODO: 포트원 API 결제 금액 검증 (포트원 연동 시 아래 주석 해제)
//        //    Step 1) POST https://api.iamport.kr/users/getToken
//        //            Body: { imp_key: ${IMP_KEY}, imp_secret: ${IMP_SECRET} }
//        //            → response.access_token 획득
//        //    Step 2) GET  https://api.iamport.kr/payments/{imp_uid}
//        //            Header: Authorization: {access_token}
//        //            → response.amount 와 req.getPaidAmount() 비교
//        //    Step 3) 금액 불일치 시 → throw new BadRequestException("결제 금액이 일치하지 않습니다.");
//        log.info("[Payment] 포트원 결제 확인 - impUid={}, merchantUid={}, amount={}",
//                req.getImpUid(), req.getMerchantUid(), req.getPaidAmount());
//
//        // 4. 수수료 계산
//        int platformFee = (int) (req.getPaidAmount() * PLATFORM_FEE_RATE);
//        int netAmount   = req.getPaidAmount() - platformFee;
//
//        // 5. Payment 저장 (에스크로 보관 - PAY-002)
////        Payment payment = Payment.builder()
//                .projectId(projectId)
//                .clientId(clientId)
//                .expertId(req.getExpertId())
//                .amount(req.getPaidAmount())
//                .platformFee(platformFee)
//                .netAmount(netAmount)
//                .paymentKey(req.getImpUid())
//                .method(req.getPayMethod())
//                .status(PaymentStatus.PAID)
//                .build();
//        paymentRepository.save(payment);
//
//        // 6. PAY-003: 프로젝트 상태 → IN_PROGRESS
//        //    Project Entity를 import하지 않고 네이티브 쿼리로 처리 (팀장 브랜치와 충돌 방지)
//        em.createNativeQuery("UPDATE project SET status = 'IN_PROGRESS' WHERE id = :pid")
//                .setParameter("pid", projectId)
//                .executeUpdate();
//
//        // 7. TODO: 전문가에게 결제 완료 알림 (보조E 담당 NotificationService 연동)
//        //    notificationService.create(req.getExpertId(), "PAYMENT", "결제가 완료되어 프로젝트가 시작되었습니다.");
//
//        log.info("[Payment] 결제 완료 - projectId={}, clientId={}, expertId={}, amount={}",
//                projectId, clientId, req.getExpertId(), req.getPaidAmount());
//
//        return PaymentResponse.from(payment);
//    }
//
//    // ===================== PAY-004: 정산 처리 =====================
//
//    /**
//     * PAY-004: 정산 처리 (프로젝트 완료 확인 후 전문가에게 netAmount 지급)
//     *
//     * @param paymentId  결제 ID
//     * @param requesterId 세션 사용자 ID (의뢰인만 가능)
//     */
//    @Transactional
//    public PaymentResponse settle(Long paymentId, Long requesterId) {
//        Payment payment = findPaymentById(paymentId);
//
//        // 의뢰인 본인만 정산 요청 가능
//        if (!Objects.equals(payment.getClientId(), requesterId)) {
//            throw new ForbiddenException("정산 권한이 없습니다.");
//        }
//
//        try {
//            payment.settle();
//        } catch (IllegalStateException e) {
//            throw new BadRequestException(e.getMessage());
//        }
//
//        // TODO: 실제 전문가 계좌 이체 API 연동 (포트원 정산 API 또는 내부 정산 시스템)
//        //    POST https://api.iamport.kr/settlements/{imp_uid}
//        log.info("[Payment] 정산 처리 - paymentId={}, expertId={}, netAmount={}",
//                paymentId, payment.getExpertId(), payment.getNetAmount());
//
//        // TODO: 전문가에게 정산 완료 알림 (보조E 담당 NotificationService 연동)
//        //    notificationService.create(payment.getExpertId(), "SETTLEMENT", "정산이 완료되었습니다.");
//
//        return PaymentResponse.from(payment);
//    }
//
//    // ===================== PAY-005: 환불 처리 =====================
//
//    /**
//     * PAY-005: 환불 처리
//     * - 프로젝트 시작 전(PAID 상태): 전액 환불
//     * - 정산 완료(SETTLED 상태): 분쟁 처리 안내 (관리자 개입 필요)
//     *
//     * @param paymentId   결제 ID
//     * @param requesterId 세션 사용자 ID (의뢰인만 가능)
//     */
//    @Transactional
//    public PaymentResponse refund(Long paymentId, Long requesterId) {
//        Payment payment = findPaymentById(paymentId);
//
//        // 의뢰인 본인만 환불 요청 가능
//        if (!Objects.equals(payment.getClientId(), requesterId)) {
//            throw new ForbiddenException("환불 권한이 없습니다.");
//        }
//
//        // 이미 정산된 경우 분쟁 처리 안내
//        if (payment.getStatus() == PaymentStatus.SETTLED) {
//            throw new BadRequestException("이미 정산이 완료된 건입니다. 분쟁 처리는 관리자에게 문의해주세요.");
//        }
//
//        try {
//            payment.refund();
//        } catch (IllegalStateException e) {
//            throw new BadRequestException(e.getMessage());
//        }
//
//        // TODO: 포트원 환불 API 호출
//        //    POST https://api.iamport.kr/payments/cancel
//        //    Body: { imp_uid: payment.getPaymentKey(), reason: "환불 요청" }
//        log.info("[Payment] 환불 처리 - paymentId={}, clientId={}, amount={}",
//                paymentId, requesterId, payment.getAmount());
//
//        // 프로젝트 상태 → CANCELLED (팀장 브랜치와 충돌 방지: 네이티브 쿼리 사용)
//        em.createNativeQuery("UPDATE project SET status = 'CANCELLED' WHERE id = :pid")
//                .setParameter("pid", payment.getProjectId())
//                .executeUpdate();
//
//        // TODO: 의뢰인/전문가에게 환불 알림 (보조E 담당 NotificationService 연동)
//        //    notificationService.create(payment.getExpertId(), "REFUND", "프로젝트 결제가 취소되었습니다.");
//
//        return PaymentResponse.from(payment);
//    }
//
//    // ===================== PAY-006: 결제 내역 조회 =====================
//
//    /**
//     * PAY-006: 의뢰인 결제 내역 조회
//     */
//    public List<PaymentResponse> getClientPayments(Long clientId) {
//        return paymentRepository.findByClientId(clientId)
//                .stream()
//                .map(PaymentResponse::from)
//                .toList();
//    }
//
//    /**
//     * PAY-006: 전문가 정산 내역 조회
//     */
//    public List<PaymentResponse> getExpertPayments(Long expertId) {
//        return paymentRepository.findByExpertId(expertId)
//                .stream()
//                .map(PaymentResponse::from)
//                .toList();
//    }
//
//    /**
//     * 프로젝트 ID로 결제 단건 조회
//     */
//    public PaymentResponse getByProjectId(Long projectId) {
//        return paymentRepository.findByProjectId(projectId)
//                .map(PaymentResponse::from)
//                .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
//    }
//
//    // ===================== private 메서드 =====================
//
//    private Payment findPaymentById(Long paymentId) {
//        return paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
//    }
//
//    /**
//     * merchantUid에서 projectId 파싱
//     * 형식: "sev-project-{projectId}"
//     */
//    private Long parseProjectId(String merchantUid) {
//        try {
//            // "sev-project-123" → ["sev", "project", "123"] → index 2
//            String[] parts = merchantUid.split("-");
//            return Long.parseLong(parts[2]);
//        } catch (Exception e) {
//            throw new BadRequestException("유효하지 않은 주문번호입니다: " + merchantUid);
//        }
//    }
//}
