package com.example.SevMerge.Report;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.Role;
import jakarta.servlet.http.HttpSession;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 관리자 댓글 신고 관리 화면 띄우기
    @GetMapping("/admin/reports")
    public String showAdminReport(@RequestParam(name = "keyword", required = false) String keyword,
                                  Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        List<ReportResponse.ListDTO> reportList = reportService.getReportListForAdmin(keyword);
        model.addAttribute("reports", reportList);
        model.addAttribute("keyword", keyword);

        return "admin/admin-report";
    }

    // 댓글 수신 처리
    @PostMapping("/comments/{id}/report")
    @ResponseBody
    public String reportComment(@PathVariable(name = "id") Long commentId,
                                @RequestParam(name = "boardId") Long boardId,
                                ReportRequest.SaveDTO saveDTO,
                                HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "<script>" +
                    "alert('로그인이 필요한 서비스입니다.');" +
                    "location.href='/login';" +
                    "</script>";
        }

        try {
            saveDTO.validate();
            reportService.submitReport(commentId, sessionUser.getId(), saveDTO);

            return "<script>" +
                    "alert('신고가 완료되었습니다.');" +
                    "location.href='/boards/" + boardId + "';" +
                    "</script>";
        } catch (BadRequestException e) {
            return "<script>" +
                    "alert('" + e.getMessage() + "');" +
                    "history.back();" +
                    "</script>";
        }
    }

    // 신고 댓글 삭제 처리
    @PostMapping("admin/reports/{commentId}/delete")
    public String deleteReport(@PathVariable(name = "commentId") Long commentId,
                                HttpSession session) {

        // 관리자 권한 검증
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        reportService.softDeleteComment(commentId);
        return "redirect:/admin/reports";
    }

    // 신고 반려 처리
    @PostMapping("admin/reports/{id}/reject")
    public String rejectReport(@PathVariable(name = "id") Long reportId,
                               HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        reportService.rejectReport(reportId);
        return "redirect:/admin/reports";
    }
}
