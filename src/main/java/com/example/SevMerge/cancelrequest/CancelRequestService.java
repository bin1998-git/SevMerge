package com.example.SevMerge.cancelrequest;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.notification.NotificationService;
import com.example.SevMerge.notification.NotificationType;
import com.example.SevMerge.payment.Payment;
import com.example.SevMerge.payment.PaymentRepository;
import com.example.SevMerge.payment.PaymentService;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.project.ProjectRepository;
import com.example.SevMerge.project.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CancelRequestService {

    private final CancelRequestRepository cancelRequestRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    @Transactional
    public void requestCancel(Long projectId, Member client, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BadRequestException("취소 사유를 입력해주세요.");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
        if (!project.getMember().getId().equals(client.getId())) {
            throw new ForbiddenException("본인 프로젝트만 취소 요청할 수 있습니다.");
        }
        if (project.getProjectStatus() != ProjectStatus.IN_PROGRESS) {
            throw new BadRequestException("진행중(낙찰된) 프로젝트만 취소할 수 있습니다.");
        }
        if (cancelRequestRepository.existsByProjectIdAndStatus(projectId, CancelStatus.PENDING)) {
            throw new BadRequestException("이미 취소 요청이 진행 중입니다.");
        }

        Payment payment = paymentRepository.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException("에스크로 결제 정보를 찾을 수 없습니다."));
        Member expert = memberRepository.findById(payment.getExpertId())
                .orElseThrow(() -> new NotFoundException("담당 전문가를 찾을 수 없습니다."));

        CancelRequest cr = CancelRequest.builder()
                .project(project)
                .requester(client)
                .expert(expert)
                .reason(reason.trim())
                .status(CancelStatus.PENDING)
                .build();
        cancelRequestRepository.save(cr);

        notificationService.notify(expert, NotificationType.MESSAGE_RECEIVED,
                "'" + project.getTitle() + "' 프로젝트에 취소 요청이 접수되었습니다. 작업관리에서 사유 확인 후 승인해주세요.",
                "/bids/my-orders");
    }

    @Transactional
    public void approveCancel(Long requestId, Member expert) {
        CancelRequest cr = cancelRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("취소 요청을 찾을 수 없습니다."));
        if (!cr.getExpert().getId().equals(expert.getId())) {
            throw new ForbiddenException("본인에게 요청된 취소만 승인할 수 있습니다.");
        }
        if (cr.getStatus() != CancelStatus.PENDING) {
            throw new BadRequestException("이미 처리된 취소 요청입니다.");
        }

        Long projectId = cr.getProject().getId();

        paymentRepository.findByProjectId(projectId).ifPresent(p ->
                paymentService.refund(p.getId(), cr.getRequester().getId()));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
        project.updateStatus(ProjectStatus.CANCELLED);
        project.delete();

        cr.approve();

        notificationService.notify(cr.getRequester(), NotificationType.REFUND_APPROVED,
                "'" + project.getTitle() + "' 프로젝트 취소가 전문가 승인으로 완료되어 환불 처리되었습니다.",
                "/my-pages?tab=projects");
    }

    @Transactional(readOnly = true)
    public List<CancelRequest> getPendingForExpert(Long expertId) {
        return cancelRequestRepository.findPendingForExpert(expertId, CancelStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public boolean hasPendingRequest(Long projectId) {
        return cancelRequestRepository.existsByProjectIdAndStatus(projectId, CancelStatus.PENDING);
    }
}
