package com.example.SevMerge.comment;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

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

    // 댓글 삭제 기능 요청
    @PostMapping("/comments/{id}/delete")
    public String delete(@PathVariable(name = "id") Long commentId,
                         HttpSession session,
                         @RequestParam(name = "boardId") Long boardId) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            return "redirect:/login";
        }

        commentService.deleteComment(commentId, sessionMember.getId());

        return "redirect:/boards/" + boardId;
    }
}
