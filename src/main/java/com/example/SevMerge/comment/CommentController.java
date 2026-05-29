package com.example.SevMerge.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록 기능 요청
    @PostMapping("/comment/save")
    public String saveProc() {

    }
}
