package com.example.SevMerge.Report;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
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
                                  @RequestParam(defaultValue = "1") int page,
                                  Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        List<ReportResponse.ListDTO> all = reportService.getReportListForAdmin(keyword);
        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("reports", s < total ? all.subList(s, e) : new java.util.ArrayList<>());
        model.addAttribute("currentPage", page); model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/admin-report";
    }

    // 댓글 수신 처리
    @PostMapping("/comments/{id}/report")
    @ResponseBody
    public String reportComment(@PathVariable(name = "id") Long commentId,
                                @RequestParam(name = "boardId") Long boardId,
                                ReportRequest.SaveDTO saveDTO,
                                HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
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
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
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
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        reportService.rejectReport(reportId);
        return "redirect:/admin/reports";
    }
}
