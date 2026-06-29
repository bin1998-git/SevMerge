package com.example.SevMerge.board;

import com.example.SevMerge.comment.CommentResponse;
import com.example.SevMerge.comment.CommentService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final com.example.SevMerge.member.MemberRepository memberRepository;

    // ── 목록 ────────────────────────────────────────────────────────
    @GetMapping("/boards")
    public String showBoard(@RequestParam(defaultValue = "FREE") String boardType,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "1") int page,
                            Model model, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", boardType.equalsIgnoreCase("INQUIRY"));
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardType", boardType);

        if (boardType.equalsIgnoreCase("INQUIRY")) {
            if (sessionUser == null) return "login-form";
            Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
            List<BoardResponse.ListDTO> list = boardService.findAllInquiry(BoardType.INQUIRY, member, null);
            model.addAttribute("boards", list);
            model.addAttribute("boardCount", list.size());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
            model.addAttribute("prevPage", null);
            model.addAttribute("nextPage", null);
            return "board/board-list";
        }

        Page<BoardResponse.ListDTO> boardPage =
                boardService.findAllByBoardType(BoardType.valueOf(boardType.toUpperCase()), keyword, page);
        model.addAttribute("boards", boardPage.getContent());
        model.addAttribute("boardCount", boardPage.getTotalElements());
        model.addAttribute("totalPages", boardPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < boardPage.getTotalPages() ? page + 1 : null);
        return "board/board-list";
    }

    // ── 상세 ────────────────────────────────────────────────────────
    @GetMapping("/boards/{boardId}")
    public String showBoardDetail(@PathVariable Long boardId, Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        boardService.increaseViewCount(boardId);

        String sessionUserRole = (sessionUser != null && sessionUser.getRole() != null)
                ? sessionUser.getRole().name() : "GUEST";
        BoardResponse.DetailDTO board = boardService.detailBoard(boardId);
        Long sessionUserId = (sessionUser != null) ? sessionUser.getId() : null;
        List<CommentResponse.ListDTO> commentList = commentService.findComments(boardId, sessionUserId, sessionUserRole);

        model.addAttribute("board", board);
        model.addAttribute("comments", commentList);
        model.addAttribute("isOwner", sessionUser != null && board.getMemberId().equals(sessionUser.getId()));
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        model.addAttribute("isNotice", "NOTICE".equalsIgnoreCase(board.getBoardType()));
        return "board/board-detail";
    }

    // ── 작성 페이지 ─────────────────────────────────────────────────
    @GetMapping("/boards/save")
    public String saveBoardPage(@RequestParam(defaultValue = "FREE") String boardType,
                                Model model, HttpSession session) throws BadRequestException {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        if (boardType.equalsIgnoreCase("NOTICE")) {
            if (sessionUser == null || sessionUser.getRole() != Role.ADMIN)
                throw new BadRequestException("공지 작성 권한이 없습니다.");
        }
        if (boardType.equalsIgnoreCase("FREE") || boardType.equalsIgnoreCase("INQUIRY")) {
            if (sessionUser == null) return "redirect:/login";
        }

        model.addAttribute("boardType", boardType);
        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", boardType.equalsIgnoreCase("INQUIRY"));
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        return "board/board-save";
    }

    // ── 작성 처리 (multipart) ────────────────────────────────────────
    @PostMapping(value = "/boards/save", consumes = "multipart/form-data")
    public String saveBoard(@ModelAttribute BoardRequest.SaveBoardDTO saveBoardDTO,
                            @RequestParam(value = "attachmentFile", required = false) MultipartFile attachmentFile,
                            HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        saveBoardDTO.setAttachmentFile(attachmentFile);
        saveBoardDTO.validate();
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        boardService.saveBoard(member, saveBoardDTO);

        return switch (saveBoardDTO.getBoardType()) {
            case INQUIRY -> "redirect:/boards?boardType=INQUIRY";
            case NOTICE -> "redirect:/boards?boardType=NOTICE";
            default -> "redirect:/boards";
        };
    }

    // ── 수정 페이지 ─────────────────────────────────────────────────
    @GetMapping("/boards/{boardId}/edit")
    public String updateBoardPage(@PathVariable Long boardId, Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        BoardResponse.DetailDTO board = boardService.detailBoard(boardId);

        model.addAttribute("board", board);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        model.addAttribute("isFree", board.getBoardType().equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", board.getBoardType().equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", board.getBoardType().equalsIgnoreCase("INQUIRY"));
        return "board/board-update";
    }

    // ── 수정 처리 (multipart) ────────────────────────────────────────
    @PostMapping(value = "/boards/{boardId}/update", consumes = "multipart/form-data")
    public String updateBoard(@PathVariable Long boardId,
                              @ModelAttribute BoardRequest.UpdateBoardDTO dto,
                              @RequestParam(value = "attachmentFile", required = false) MultipartFile attachmentFile,
                              HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        boardService.updateBoard(boardId, dto, sessionUser.getId(), attachmentFile);
        return "redirect:/boards/" + boardId;
    }

    // ── 삭제 ────────────────────────────────────────────────────────
    @DeleteMapping("/boards/{boardId}")
    @ResponseBody
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        boardService.deleteBoard(boardId, sessionUser.getId());
        return ResponseEntity.ok().build();
    }

    // ── 관리자 ──────────────────────────────────────────────────────
    @GetMapping("/admin/boards")
    public String AdminBoards(@RequestParam(defaultValue = "FREE") String boardType,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") int page,
                              Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);
        List<BoardResponse.ListDTO> all =
                boardService.getAdminBoardsByType(BoardType.valueOf(boardType.toUpperCase()), keyword);
        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("boards", s < total ? all.subList(s, e) : new java.util.ArrayList<>());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        model.addAttribute("isFree", boardType.equalsIgnoreCase("FREE"));
        model.addAttribute("isNotice", boardType.equalsIgnoreCase("NOTICE"));
        model.addAttribute("isInquiry", boardType.equalsIgnoreCase("INQUIRY"));
        model.addAttribute("boardType", boardType);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/admin-board";
    }

    @PostMapping("/admin/boards/{boardId}/delete")
    public String deleteBoardByAdmin(@PathVariable Long boardId, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) return "redirect:/admin/boards";
        boardService.deleteBoardByAdmin(boardId);
        return "redirect:/admin/boards";
    }

    @GetMapping("/admin/notices/write")
    public String noticeWriteForm(Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) return "redirect:/login";
        model.addAttribute("isAdmin", true);
        model.addAttribute("isNotice", true);
        model.addAttribute("isFree", false);
        model.addAttribute("board", Board.builder().title("").content("").viewCount(0).isActive(true).build());
        return "admin/admin-noticewrite";
    }

    @PostMapping("/admin/notices/write")
    public String noticeWrite(@RequestParam String title, @RequestParam String content, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) return "redirect:/login";
        boardService.createNotice(title, content, sessionUser.getId());
        return "redirect:/admin/notices";
    }

    @PostMapping("/admin/notices/{boardId}/delete")
    public String deleteNoticeByAdmin(@PathVariable Long boardId, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) return "redirect:/login";
        boardService.deleteNotice(boardId);
        return "redirect:/admin/notices";
    }

    @GetMapping("/admin/inquiry")
    public String adminInquiryList(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) return "redirect:/login";
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        List<BoardResponse.ListDTO> all = boardService.findAllInquiry(BoardType.INQUIRY, member, null);
        if (keyword != null && !keyword.isEmpty()) {
            all = all.stream()
                    .filter(board -> (board.getTitle() != null && board.getTitle().contains(keyword)) ||
                            (board.getContent() != null && board.getContent().contains(keyword))).toList();
        }
        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("boards", s < total ? all.subList(s, e) : new java.util.ArrayList<>());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        return "admin/admin-inquiry";
    }
}
