package com.example.SevMerge.Report;

import com.example.SevMerge.comment.Comment;
import com.example.SevMerge.comment.CommentRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (commentEntity.getMember().getId().equals(sessionUserId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글은 신고할 수 없습니다.");
        }

        reportRepository.findByCommentIdAndReporterId(commentId, sessionUserId)
                .ifPresent(report -> {
                    throw new IllegalArgumentException("이미 신고 접수가 완료된 댓글입니다.");
                });

        Member reporterEntity = memberRepository.findById(sessionUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Report report = Report.builder()
                .comment(commentEntity)
                .reporter(reporterEntity)
                .reason(saveDTO.getReason())
                .contentDetail(saveDTO.getContentDetail())
                .build();

        reportRepository.save(report);
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
}
