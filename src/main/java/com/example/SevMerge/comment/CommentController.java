package com.example.SevMerge.comment;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록 기능 요청
    @PostMapping("/comment/save")
    public String saveProc(CommentRequest.SaveDTO saveDTO, HttpSession session) {
        // 인증검사
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        // 유효성 검사
        saveDTO.validate();

        commentService.createComment(saveDTO, sessionMember.getId());

        // 해당 게시글에 댓글 작성 후 리다이렉션 처리 (해당 게시글로)
        return "redirect:/boards/" + saveDTO.getBoardId();
    }

    // 댓글 삭제 기능 요청
    @PostMapping("/comment/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer commentId,
                         @RequestParam(name = "boardId") long boardId,
                         HttpSession session) {
        // 1. 인증검사 (인터셉터)
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        commentService.deleteComment(commentId,sessionMember.getId());

        return "redirect:/boards/" + boardId;
    }
}
