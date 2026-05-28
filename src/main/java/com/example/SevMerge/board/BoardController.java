package com.example.SevMerge.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/list")
    public String list(Type type) {

        boardService.게시판목록(type);

        // TODO 메스테치 파일 나오면 넣기
        return "/board/";
    }

}
