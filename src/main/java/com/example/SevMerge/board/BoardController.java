package com.example.SevMerge.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;;

    // todo: 추후 메인 페이지 요청하는 곳 생성되면 삭제예정
    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    @GetMapping("/boards")
    // 게시판 메인 화면
    public String showBoard(Model model) {

        model.addAttribute("boards");

        return "board/board";
    }

    @GetMapping("/boards/save")
    public String saveBoardPage() {

        return "board/board-save";
    }
}
