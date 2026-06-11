package com.example.SevMerge.bid;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.payment.Payment;
import com.example.SevMerge.payment.PaymentRepository;
import com.example.SevMerge.payment.PaymentService;
import com.example.SevMerge.project.BidFilter;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.project.ProjectRepository;
import com.example.SevMerge.project.ProjectStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BidService {

    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    // 제안서 작성
    @Transactional
    public void saveBid(BidRequestDTO.SaveDTO req, Member session) {
        log.info("제안 작성 서비스 시작");
        // 전문가 여부 체크
        if (!session.isExpert()) {
            throw new ForbiddenException("전문가만 제안서를 작성할 수 있습니다.");
        }

        // 유효성 검사
        req.validate();

        // 프로젝트가 있는지 확인
        Project project = projectRepository.findByProjectId(req.getProjectId()).orElseThrow(
                () -> new NotFoundException("존재하지 않는 프로젝트 입니다"));

        // 프로젝트가 입찰됬는지 안되었는지 상태 확인
        if (project.getProjectStatus() != ProjectStatus.OPEN) {
            throw new BadRequestException("입찰된 프로젝트 입니다");
        }

        // 중복입찰 체크
        bidRepository.findByProjectIdAndExpertId(req.getProjectId(), session.getId())
                .ifPresent(b -> {
                    throw new BadRequestException("이미 입찰한 프로젝트입니다.");
                });

        // 인증된 전문가만 받을수 있게 체크
        if (project.getBidFilter() == BidFilter.CERTIFIED_ONLY) {
            if (!session.isActiveExpert()) {
                throw new ForbiddenException("인증된 전문가만 입찰 받을수 있습니다");
            }
        }


        Bid bid = Bid.builder()
                .project(project)
                .expert(session)
                .coverLetter(req.getCoverLetter())
                .approach(req.getApproach())
                .estimatedDays(req.getEstimatedDays())
                .proposedPrice(req.getProposedPrice())
                .build();
        bidRepository.save(bid);

    }

    // 제안서 조회 (의뢰인)
    public List<BidResponseDTO.ListDTO> findByProjectId(Long projectId, Member session) {
        log.info("제안서 조회 서비스 시작");
        // 의뢰인 여부 체크
        if (!session.isClient() && !session.isAdmin()) {
            throw new ForbiddenException("의뢰인만 제안서 목록을 조회할 수 있습니다.");
        }
        List<Bid> bidList = bidRepository.findByProjectId(projectId);
        return bidList.stream()
                .map(BidResponseDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 제안서 조회(전문가)
    public List<BidResponseDTO.ListDTO> findMyBids(Member session) {
        log.info("내 제안서 조회 서비스 시작");
        // 전문가 여부 체크
        if (!session.isExpert()) {
            throw new ForbiddenException("전문가만 제안서를 조회할 수 있습니다.");
        }
        List<Bid> bidList = bidRepository.findByExpertId(session.getId());
        return bidList.stream()
                .map(BidResponseDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 주문 목록 조회(전문가) — SELECTED 낙찰 건만 반환
    public List<BidResponseDTO.OrderDTO> findMyOrders(Member session) {
        log.info("내 주문 목록 조회 서비스 시작");
        if (!session.isExpert()) {
            throw new ForbiddenException("전문가만 주문 내역을 조회할 수 있습니다.");
        }
        List<Bid> selectedBids = bidRepository.findByExpertId(session.getId())
                .stream()
                .filter(b -> b.getStatus() == BidStatus.SELECTED)
                .collect(Collectors.toList());

        return selectedBids.stream()
                .map(bid -> {
                    Payment payment = paymentRepository
                            .findByProjectId(bid.getProject().getId())
                            .orElse(null);
                    return new BidResponseDTO.OrderDTO(bid, payment);
                })
                .collect(Collectors.toList());
    }

    // 제안서 수정
    @Transactional
    public void updateBid(Long id, BidRequestDTO.UpdateDTO req, Member session) {
        log.info("제안서 수정 서비스 시작");
        // 유효성검사
        req.validate();

        // 전문가 여부 체크
        if (!session.isExpert()) {
            throw new ForbiddenException("전문가가 아닙니다 수정이 불가능합니다");
        }

        Bid bid = bidRepository.findById(id).orElseThrow(
                () -> new NotFoundException("존재하지 않는 제안서입니다"));


        // 내가 낸 제안서인지 확인
        if (!bid.getExpert().getId().equals(session.getId())) {
            throw new ForbiddenException("제안서 수정 권한이 없습니다");
        }
        // 제안서 확인 체크
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new BadRequestException("처리된 제안서는 수정이 불가능합니다");
        }

        bid.update(req);
    }

    // 제안서 취소
    @Transactional
    public void deleteBid(Long id, Member session) {
        log.info("제안서 삭제 서비스 시작");

        // 전문가 체크
        if (!session.isExpert()) {
            throw new ForbiddenException("취소할 권한은 전문가만 있습니다");
        }

        Bid bid = bidRepository.findById(id).orElseThrow(
                () -> new NotFoundException("취소할 제안서가 존재하지 않습니다"));

        // 내가낸 제안서인지 체크
        if (!bid.getExpert().getId().equals(session.getId())) {
            throw new ForbiddenException("취소권한이 있는 제안서가 아닙니다");
        }

        if (bid.getStatus() != BidStatus.PENDING) {
            throw new BadRequestException("처리된 제안서는 취소가 불가능합니다");
        }

        bid.delete();
    }


    // 낙찰 처리
    @Transactional
    public void selectBid(Long bidId, Member session) {
        log.info("낙찰 서비스 시작");

        // 의뢰인 여부 체크
        if (!session.isClient()) {
            throw new ForbiddenException("낙찰처리는 의뢰인만 가능합니다");
        }

        Bid bid = bidRepository.findById(bidId).orElseThrow(
                () -> new NotFoundException("낙찰처리할 제안서가 없습니다"));

        // 프로젝트 가진사람 체크
        if (!bid.getProject().getMember().getId().equals(session.getId())) {
            throw new ForbiddenException("낙찰처리 권한이 없습니다");
        }

        // 제안서 상태를 SELECTED로 변경
        bid.select();

        // 나머지 대기중인 제안서 전부 탈락 처리
        List<Bid> otherBids = bidRepository.findByProjectId(bid.getProject().getId());
        for (Bid other : otherBids) {
            if (!other.getId().equals(bid.getId()) &&
                    (other.getStatus() == BidStatus.PENDING || other.getStatus() == BidStatus.HOLD)) {
                other.fail();
            }
        }

        // 에스크로 생성: 잔액 체크 + 차감 + Payment(PAID) 저장 + 프로젝트 IN_PROGRESS 전환
        paymentService.createEscrow(
                session.getId(),
                bid.getProject().getId(),
                bid.getExpert().getId(),
                bid.getProposedPrice().intValue()
        );
    }

    // 제안서 보류처리
    @Transactional
    public void holdBid(Long bidId, Member session) {
        log.info("제안서 보류 서비스 시작 : bidId {}" + bidId);

        // 의뢰인 체크
        if (!session.isClient()) {
            throw new ForbiddenException("보류 처리는 의뢰인만 가능합니다");
        }

        // 제안서 여부 체크
        Bid bid = bidRepository.findById(bidId).orElseThrow(
                () -> new NotFoundException("보류 처리할 제안서가 없습니다"));

        // 프로젝트를 올린 의뢰인이 맞는지 체크
        if (!bid.getProject().getMember().getId().equals(session.getId())) {
            throw new ForbiddenException("보류 처리할 권한이 없습니다");
        }

        // 낙찰처리, 거절된 제안서인지 체크
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new BadRequestException("대기 상태인 제안서만 보류처리가 가능합니다");
        }

        // 제안서 상태 HOLD변경
        bid.hold();

        log.info("제안서 보류 처리 완료 - bidId: {}", bidId);
    }




    public BidResponseDTO.DetailDTO findBidById(Long id, Member session) {
        log.info("제안서 상세 조회 서비스 시작");
        Bid bid = bidRepository.findById(id).orElseThrow(
                () -> new NotFoundException("존재하지 않는 제안서입니다"));

        // 글쓴 본인, 프로젝트를 올린 의뢰인이 통과
        boolean isWriter = bid.getExpert().getId().equals(session.getId());
        boolean Owner = bid.getProject().getMember().getId().equals(session.getId());

        if (!isWriter && !Owner) {
            throw new ForbiddenException("해당 제안서를 조회할 권한이 없습니다");
        }

        return new BidResponseDTO.DetailDTO(bid);
    }

    // 제안서 거절
    @Transactional
    public void rejectBid(Long id, Member session) {
        log.info("제안서 거절 서비스 시작");

        Bid bid = bidRepository.findById(id).orElseThrow(
                () -> new NotFoundException("존재하지 않는 제안서 입니다"));

        // 의뢰인 체크
        if (!session.isClient()) {
            throw new ForbiddenException("의뢰인만 제안서를 거절할 수 있습니다");
        }

        // 프로젝트를 의뢰한 사람인지 체크
        if (!bid.getProject().getMember().getId().equals(session.getId())) {
            throw new ForbiddenException("프로젝트를 제안한 의뢰인이 아닙니다");
        }
        // 처리가 된 제안서인지 체크
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new BadRequestException("처리가 된 제안서 입니다");
        }
        bid.reject();
    }

    public Optional<Bid> findSelectedBidByProjectId(Long projectId) {
        return bidRepository.findSelectedBidByProjectId(projectId, BidStatus.SELECTED);
    }
}
