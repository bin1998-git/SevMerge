package com.example.SevMerge.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/list")
    public String list(Type type, Model model) {

         List<Board> boardList = boardService.게시판목록(type);
        model.addAttribute("board",boardList);
        // TODO 메스테치 파일 나오면 넣기
        return "/board/";
    }

}
