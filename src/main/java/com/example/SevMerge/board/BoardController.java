package com.example.SevMerge.board;

import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

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
    public String showBoard(@RequestParam(defaultValue = "FREE") String boardType,
                                Model model) {

        List<Board> boardList = new ArrayList<>();

        model.addAttribute("isFree",boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice",boardType.equalsIgnoreCase("NOTICE"));

        if (boardType.equalsIgnoreCase("FREE")){
            boardList = boardService.findAllByBoardType(BoardType.FREE);
        } else if(boardType.equalsIgnoreCase("NOTICE")) {
            boardList = boardService.findAllByBoardType(BoardType.NOTICE);
        }

        model.addAttribute("boards",boardList);

        return "board/board";
    }

    @GetMapping("/boards/save")
    public String saveBoardPage(@RequestParam(defaultValue = "FREE") String boardType,
                                Model model) {

        model.addAttribute("boardType",boardType);
        model.addAttribute("isFree",boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice",boardType.equalsIgnoreCase("NOTICE"));

        return "board/board-save";
    }

    @PostMapping("boards/save")
    public String saveBoard(BoardRequest.SaveBoardDTO saveBoardDTO,
                            HttpSession session) {
        Member sessionMember = (Member) session.getAttribute("sessionMember");
        boardService.saveBoard(sessionMember,saveBoardDTO);

        return "redirect:/boards";
    }

    @GetMapping("/boards/{boardId}/update")
    public String updateBoardPage() {
        return "board/board-update";
    }

    @PostMapping("/boards/{boardId}/update")
    public String updateBoard() {
        return "redirect:/board";
    }
}
