package com.example.SevMerge.comment;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록 기능 요청
    @PostMapping("/boards/{boardId}/comments/save")
    public String saveProc(@PathVariable("boardId") Long boardId,
                           CommentRequest.SaveDTO saveDTO, HttpSession session) {
        // 인증검사
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        // 유효성 검사
        saveDTO.validate();

        commentService.createComment(saveDTO, sessionMember.getId());

        // 해당 게시글에 댓글 작성 후 리다이렉션 처리 (해당 게시글로)
        return "redirect:/boards/" + boardId;
    }

    // 댓글 수정 기능
    @PostMapping("/comments/{commentId}/update")
    public String updateComment(@PathVariable(name = "commentId") Long commentId,
                                @ModelAttribute CommentRequest.SaveDTO.UpdateDTO updateDTO, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            return "redirect:/boards/" + commentId;
        }

        commentService.updateComment(commentId, updateDTO.getComment(), sessionMember.getId());
        return "redirect:/boards/" + updateDTO.getBoardId();
    }

    // 댓글 삭제 기능
    @PostMapping("/comments/{id}/delete")
    public String delete(@PathVariable(name = "id") Long commentId,
                         HttpSession session,
                         @RequestParam(name = "boardId") Long boardId) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            return "redirect:/login";
        }

        if (sessionMember.getRole() == Role.ADMIN) {
            commentService.deleteCommentByAdmin(commentId);
            return "redirect:/admin/comments";
        }
        commentService.deleteComment(commentId, sessionMember.getId());

        return "redirect:/boards/" + boardId;
    }

    // 관리자 댓글 전체조회
    @GetMapping("/admin/comments")
    public String showAdminComments(@RequestParam(name = "keyword", required = false) String keyword,
                                    Model model, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null || sessionMember.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        List<CommentResponse.ListDTO> commentList = commentService.findAllCommentsForAdmin(keyword);
        model.addAttribute("comments", commentList);
        model.addAttribute("keyword", keyword);
        return "admin/admin-comment";
    }
}
