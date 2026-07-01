package com.example.SevMerge.deliverable;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.util.FileUtil;
import com.example.SevMerge.member.Member;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliverableService {

    private final DeliverableRepository deliverableRepository;
    private final ProjectRepository projectRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    @Transactional
    public void submit(Long projectId, MultipartFile file, String note, boolean isFinal, Member expert) throws IOException {
        if (!expert.isExpert()) {
            throw new ForbiddenException("전문가만 작업물을 제출할 수 있습니다.");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
        if (project.getProjectStatus() != ProjectStatus.IN_PROGRESS) {
            throw new BadRequestException("진행중인 프로젝트에만 작업물을 제출할 수 있습니다.");
        }
        Payment payment = paymentRepository.findByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException("에스크로 결제 정보를 찾을 수 없습니다."));
        if (!payment.getExpertId().equals(expert.getId())) {
            throw new ForbiddenException("낙찰된 전문가만 작업물을 제출할 수 있습니다.");
        }
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("제출할 파일을 첨부해주세요.");
        }

        String savedName = FileUtil.saveFile(file, FileUtil.IMAGES_DIR);
        int round = (int) deliverableRepository.countByProjectId(projectId) + 1;

        Deliverable d = Deliverable.builder()
                .project(project)
                .expert(expert)
                .round(round)
                .fileUrl(savedName)
                .fileName(file.getOriginalFilename())
                .note(note)
                .isFinal(isFinal)
                .status(DeliverableStatus.SUBMITTED)
                .build();
        deliverableRepository.save(d);

        notificationService.notify(project.getMember(), NotificationType.PAYMENT_COMPLETED,
                expert.getName() + " 전문가가 '" + project.getTitle() + "' " + round + "차 작업물"
                        + (isFinal ? "(최종본)" : "") + "을 제출했습니다. 확인 후 승인 또는 보완을 요청해주세요.",
                "/projects/" + projectId);
    }

    @Transactional
    public void requestRevision(Long deliverableId, String reason, Member client) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BadRequestException("보완 요청 사유를 입력해주세요.");
        }
        Deliverable d = loadOwnedDeliverable(deliverableId, client);
        if (d.getStatus() == DeliverableStatus.APPROVED) {
            throw new BadRequestException("이미 승인된 작업물입니다.");
        }
        d.requestRevision(reason.trim());
        notificationService.notify(d.getExpert(), NotificationType.PAYMENT_COMPLETED,
                "'" + d.getProject().getTitle() + "' " + d.getRound() + "차 작업물에 보완 요청이 도착했습니다: " + reason.trim(),
                "/bids/my-orders");
    }

    @Transactional
    public void approve(Long deliverableId, Member client) {
        Deliverable d = loadOwnedDeliverable(deliverableId, client);
        if (d.getStatus() == DeliverableStatus.APPROVED) {
            throw new BadRequestException("이미 승인된 작업물입니다.");
        }
        d.approve();
        Project project = d.getProject();

        if (d.getIsFinal()) {
            Payment payment = paymentRepository.findByProjectId(project.getId())
                    .orElseThrow(() -> new NotFoundException("에스크로 결제 정보를 찾을 수 없습니다."));
            paymentService.settle(payment.getId(), client.getId());
            notificationService.notify(d.getExpert(), NotificationType.PAYMENT_COMPLETED,
                    "'" + project.getTitle() + "' 최종 작업물이 승인되어 정산이 완료되었습니다.",
                    "/bids/my-orders");
        } else {
            notificationService.notify(d.getExpert(), NotificationType.PAYMENT_COMPLETED,
                    "'" + project.getTitle() + "' " + d.getRound() + "차 작업물이 승인되었습니다.",
                    "/bids/my-orders");
        }
    }

    @Transactional(readOnly = true)
    public List<DeliverableResponse.ListDTO> getByProject(Long projectId) {
        return deliverableRepository.findByProjectIdOrderByRound(projectId).stream()
                .map(DeliverableResponse.ListDTO::new)
                .toList();
    }

    private Deliverable loadOwnedDeliverable(Long deliverableId, Member client) {
        Deliverable d = deliverableRepository.findById(deliverableId)
                .orElseThrow(() -> new NotFoundException("작업물을 찾을 수 없습니다."));
        if (!d.getProject().getMember().getId().equals(client.getId())) {
            throw new ForbiddenException("본인 프로젝트의 작업물만 처리할 수 있습니다.");
        }
        return d;
    }
}
