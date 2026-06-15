package com.example.SevMerge.board;

import com.example.SevMerge.comment.CommentResponse;
import com.example.SevMerge.comment.CommentService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.expertprofile.ExpertProfileService;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberResponse;
import com.example.SevMerge.member.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final ExpertProfileService expertProfileService;

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

    @GetMapping("/intro")
    public String introPage() {
        return "intro";
    }

    @GetMapping("/exmain")
    public String exmainPage(Model model) {
        List<ExpertProfileResponse> all = expertProfileService.getAll();

        // 섹션 1 — 오분대기조: avgRating 높은 순 상위 6명
        List<ExpertProfileResponse> fastExperts = all.stream()
                .sorted(Comparator.comparing(ExpertProfileResponse::getAvgRating).reversed())
                .limit(6)
                .toList();

        // 섹션 2 — 바가지 수사대: totalReviews(완료 건수) 많은 순 상위 6명
        List<ExpertProfileResponse> valueExperts = all.stream()
                .sorted(Comparator.comparingInt(ExpertProfileResponse::getTotalReviews).reversed())
                .limit(6)
                .toList();

        // 섹션 3 — 만족 취조실: isCertified 우선 정렬, 그 다음 avgRating 순
        List<ExpertProfileResponse> asExperts = all.stream()
                .sorted(Comparator.comparing(ExpertProfileResponse::isCertified).reversed()
                        .thenComparing(Comparator.comparing(ExpertProfileResponse::getAvgRating).reversed()))
                .limit(6)
                .toList();

        model.addAttribute("fastExperts", fastExperts);
        model.addAttribute("valueExperts", valueExperts);
        model.addAttribute("asExperts", asExperts);
        return "exmain";
    }

    @GetMapping("/boards")
    public String showBoard(@RequestParam(defaultValue = "FREE") String boardType,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "1") int page,
                            Model model, HttpSession session) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", boardType.equalsIgnoreCase("INQUIRY"));
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardType", boardType);

        // 1:1 문의 분기
        if (boardType.equalsIgnoreCase("INQUIRY")) {
            if (sessionUser == null) return "login-form";
            List<BoardResponse.ListDTO> inquiryBoards = boardService.findAllInquiry(BoardType.INQUIRY, sessionUser);
            model.addAttribute("boards", inquiryBoards);
            model.addAttribute("boardCount", inquiryBoards.size());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
            model.addAttribute("prevPage", null);
            model.addAttribute("nextPage", null);
            return "board/board-list";
        }

        // 일반 게시판
        Page<BoardResponse.ListDTO> boardPage = boardService.findAllByBoardType(BoardType.valueOf(boardType.toUpperCase()), keyword, page);
        model.addAttribute("boards", boardPage.getContent());
        model.addAttribute("boardCount", boardPage.getTotalElements());
        model.addAttribute("totalPages", boardPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < boardPage.getTotalPages() ? page + 1 : null);

        return "board/board-list";
    }

    @GetMapping("/boards/{boardId}")
    public String showBoardDetail(@PathVariable(name = "boardId") Long boardId,
                                  Model model, HttpSession session) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        boardService.increaseViewCount(boardId);
        String sessionUserRole = (sessionUser != null && sessionUser.getRole() != null) ? sessionUser.getRole().name() : "GUEST";
        BoardResponse.DetailDTO board = boardService.detailBoard(boardId);
        Long boardOwner = board.getMemberId();
        Long sessionUserId = (sessionUser != null) ? sessionUser.getId() : null;
        List<CommentResponse.ListDTO> commentList = commentService.findComments(boardId,sessionUserId, sessionUserRole);
        model.addAttribute("board", board);
        model.addAttribute("comments", commentList);
        model.addAttribute("isOwner", sessionUser != null && boardOwner.equals(sessionUser.getId()));
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        boolean isNoticeBoard = "NOTICE".equals(board.getBoardType()) || "NOTICE".equals(String.valueOf(board.getBoardType()));
        model.addAttribute("isNotice", isNoticeBoard);
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
                return "login-form";
            }
        }

        model.addAttribute("boardType", boardType);
        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", boardType.equalsIgnoreCase("INQUIRY"));
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        return "board/board-save";
    }

    @PostMapping("boards/save")
    public String saveBoard(BoardRequest.SaveBoardDTO saveBoardDTO,
                            HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        saveBoardDTO.validate();

        boardService.saveBoard(sessionUser, saveBoardDTO);

        if (saveBoardDTO.getBoardType() == BoardType.INQUIRY) {
            return "redirect:/boards?boardType=INQUIRY";
        } else if (saveBoardDTO.getBoardType() == BoardType.NOTICE) {
            return "redirect:/boards?boardType=NOTICE";
        }
        return "redirect:/boards";
    }

    @GetMapping("/boards/{boardId}/edit")
    public String updateBoardPage(@PathVariable(name = "boardId") Long boardId,
                                  Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        BoardResponse.DetailDTO board = boardService.detailBoard(boardId);
        model.addAttribute("board", board);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);


        return "board/board-update";
    }

    @PutMapping("/boards/{boardId}")
    @ResponseBody
    public ResponseEntity<?> updateBoard(@PathVariable(name = "boardId") Long boardId,
                                         @RequestBody BoardRequest.UpdateBoardDTO updateBoardDTO,
                                         HttpSession session) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        boardService.updateBoard(boardId, updateBoardDTO, sessionUser.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/boards/{boardId}")
    @ResponseBody
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        boardService.deleteBoard(boardId, sessionUser.getId());
        return ResponseEntity.ok().build();
    }

    // 관리자 자유 게시판 관리
    @GetMapping("/admin/boards")
    public String AdminBoards(@RequestParam(defaultValue = "FREE") String boardType,
                              @RequestParam(value = "keyword", required = false) String keyword, Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        // 관리자가 로그인하면 상단바에 마이페이지가 아닌 관리자가 뜨게 만들기
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        BoardType type = BoardType.valueOf(boardType.toUpperCase());
        List<BoardResponse.ListDTO> adminBoards = boardService.getAdminBoardsByType(type, keyword);

        model.addAttribute("boards", adminBoards);
        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", boardType.equalsIgnoreCase("INQUIRY"));
        model.addAttribute("boardType", boardType);
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "admin/admin-board";
    }

    // 관리자 게시판삭제
    @PostMapping("/admin/boards/{boardId}/delete")
    public String deleteBoardByAdmin(@PathVariable(name = "boardId") Long boardId, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/admin/boards";
        }

        boardService.deleteBoardByAdmin(boardId);
        return "redirect:/admin/boards";
    }
}
