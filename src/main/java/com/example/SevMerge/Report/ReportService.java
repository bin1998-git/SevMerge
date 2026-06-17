package com.example.SevMerge.Report;

import com.example.SevMerge.comment.Comment;
import com.example.SevMerge.comment.CommentRepository;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BlacklistRepository blacklistRepository;

    @Transactional(readOnly = true)
    public List<ReportResponse.ListDTO> getReportListForAdmin(String keyword) {
        List<Report> reports = reportRepository.findAllWithDetails(keyword);
        return reports.stream()
                .map(ReportResponse.ListDTO::new)
                .toList();
    }

    @Transactional
    public void submitReport(Long commentId, Long sessionUserId, ReportRequest.SaveDTO saveDTO) {
        Comment commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당 댓글이 존재하지 않습니다."));

        if (commentEntity.getMember().getId().equals(sessionUserId)) {
            throw new BadRequestException("본인이 작성한 댓글은 신고할 수 없습니다.");
        }

        reportRepository.findByCommentIdAndReporterId(commentId, sessionUserId)
                .ifPresent(report -> {
                    throw new BadRequestException("이미 신고 접수가 완료된 댓글입니다.");
                });

        Member reporterEntity = memberRepository.findById(sessionUserId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Report report = Report.builder()
                .comment(commentEntity)
                .reporter(reporterEntity)
                .reason(saveDTO.getReason())
                .contentDetail(saveDTO.getContentDetail())
                .build();

        reportRepository.save(report);

        Member reportedMember = commentEntity.getMember();
        reportedMember.addReport();

        if (reportedMember.getReportCount() >= 3) {
            List<Report> allReports = reportRepository.findAll().stream()
                    .filter(report1 -> report1.getComment().getMember().getId().equals(reportedMember.getId()))
                    .toList();
            List<String> reportIdList = new ArrayList<>();
            for (Report r : allReports) {
                reportIdList.add(String.valueOf(r.getId()));
            }
            String accumulatedReportIds = String.join(",", reportIdList);

            // 차단 기간 설정 7일 정지
            LocalDateTime expireDate = LocalDateTime.now().plusDays(7);

            reportedMember.changeStatusByBlacklist(Status.SUSPENDED);

            BlackList blackList = BlackList.builder()
                    .member(reportedMember)
                    .reason("댓글 신고 3회 누적 자동 정지 처리")
                    .reportIds(accumulatedReportIds)
                    .expiredAt(expireDate)
                    .isActive(true)
                    .build();

            blacklistRepository.save(blackList);
            blacklistRepository.flush();
        }
    }

    // 신고 댓글 삭제
    @Transactional
    public void softDeleteComment(Long commentId) {
        List<Report> reports = reportRepository.findByCommentId(commentId);
        if (reports.isEmpty()) {
            throw new IllegalArgumentException("해당 댓글에 대한 신고 내역을 찾을 수 없습니다.");
        }

        Comment commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        commentEntity.setDeleted(true);

        for (Report report : reports) {
            report.completeProcessing();
        }

    }

    // 신고 내역 반려처리 / 원본 댓글은 삭제안됨
    @Transactional
    public void rejectReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고내역이 존재하지 않습니다. id = " + reportId));

        report.completeProcessing();
    }

    // 차단된 회원 정지해제하는거
    @Transactional
    public void releaseMember(Long memberId) {
        Member reportedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 회원을 찾을 수 없습니다."));

        // 정상인으로 돌려놓기
        reportedMember.changeStatusByBlacklist(Status.ACTIVE);

        // 신고횟수 다시 0으로 되돌려놓기
        reportedMember.resetReportCount();
        // 차단된놈 다시 부활
        List<BlackList> activeBlacklists = blacklistRepository.findByMemberIdAndIsActiveTrue(memberId);
        for (BlackList blackList : activeBlacklists) {
            blackList.release("관리자 권한으로 정지해제 되었습니다.");
        }
    }
}
