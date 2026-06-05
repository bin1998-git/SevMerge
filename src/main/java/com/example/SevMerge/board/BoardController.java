package com.example.SevMerge.board;

import com.example.SevMerge.comment.CommentResponse;
import com.example.SevMerge.comment.CommentService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    // todo: 추후 메인 페이지 요청하는 곳 생성되면 삭제예정
    @GetMapping("/")
    public String mainPage(Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        if (sessionUser != null && sessionUser.getRole() == Role.ADMIN) {
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }

            return "main";
    }

    @GetMapping("/boards")
    public String showBoard(@RequestParam(defaultValue = "FREE") String boardType,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "1") int page,
                            Model model, HttpSession session) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        // 페이징 + 키워드 서비스 호출
        Page<BoardResponse.ListDTO> boardPage = boardService.findAllByBoardType(BoardType.valueOf(boardType.toUpperCase()), keyword, page);

        // todo-추후 페이지 번호 추가
        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", boardType.equalsIgnoreCase("INQUIRY"));
        model.addAttribute("isAdmin", sessionMember != null && sessionMember.getRole() == Role.ADMIN);
        model.addAttribute("boards", boardPage.getContent());
        model.addAttribute("boardCount", boardPage.getTotalElements());
        model.addAttribute("totalPages", boardPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < boardPage.getTotalPages() ? page + 1 : null);
        model.addAttribute("boardType", boardType);

        return "board/board-list";
    }

    @GetMapping("/boards/{boardId}")
    public String showBoardDetail(@PathVariable(name = "boardId") Long boardId,
                                  Model model, HttpSession session) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        BoardResponse.DetailDTO board = boardService.detailBoard(boardId);
        Long boardOwner = board.getMemberId();
        List<CommentResponse.ListDTO> commentList = commentService.findComments(boardId);
        model.addAttribute("board", board);
        model.addAttribute("comments", commentList);
        model.addAttribute("isOwner",sessionMember!=null && boardOwner.equals(sessionMember.getId()));
        return "board/board-detail";
    }

    @GetMapping("/boards/save")
    public String saveBoardPage(@RequestParam(defaultValue = "FREE") String boardType,
                                Model model, HttpSession session) throws BadRequestException {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        // 공지사항은 어드민만
        if (boardType.equalsIgnoreCase("NOTICE")) {
            if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
                throw new BadRequestException("공지 작성 권한이 없습니다.");
            }
        }

        // 자유게시판은 로그인만 되어있으면 됨
        if (boardType.equalsIgnoreCase("FREE") ||
                boardType.equalsIgnoreCase("INQUIRY")) {
            if (sessionUser == null) {
                return "redirect:/login-form";
            }
        }

        model.addAttribute("boardType", boardType);
        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry",boardType.equalsIgnoreCase("INQUIRY"));

        return "board/board-save";
    }

    @PostMapping("boards/save")
    public String saveBoard(BoardRequest.SaveBoardDTO saveBoardDTO,
                            HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        saveBoardDTO.validate();

        boardService.saveBoard(sessionMember, saveBoardDTO);

        return "redirect:/boards";
    }

    @GetMapping("/boards/{boardId}/edit")
    public String updateBoardPage(@PathVariable(name = "boardId") Long boardId,
                                  Model model) {

        BoardResponse.DetailDTO board = boardService.detailBoard(boardId);
        model.addAttribute("board",board);

        return "board/board-update";
    }

    @PostMapping("/boards/{boardId}/edit")
    public String updateBoard(@PathVariable(name = "boardId") Long boardId,
                              BoardRequest.UpdateBoardDTO updateBoardDTO,
                              HttpSession session) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        boardService.updateBoard(boardId,updateBoardDTO,sessionMember.getId());
        return "redirect:/boards";
    }

    @PostMapping("/boards/{boardId}/delete")
    public String deleteBoard(@PathVariable(name = "boardId") Long boardId,
                              HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        boardService.deleteBoard(boardId,sessionMember.getId());
        return "redirect:/boards";
    }

    // 관리자 게시판 관리
    @GetMapping("/admin/boards")
    public String AdminBoards() {

        return "admin/admin-board";
    }
}
