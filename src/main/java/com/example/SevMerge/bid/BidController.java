package com.example.SevMerge.bid;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.SessionUser;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final ProjectService projectService;
    private final com.example.SevMerge.cancelrequest.CancelRequestService cancelRequestService;
    private final com.example.SevMerge.member.MemberRepository memberRepository;

    // 1. 제안서 작성 폼
    @GetMapping("/bids/save-form")
    public String saveForm(@RequestParam Long projectId, Model model, HttpSession session) {
        log.info("제안서 등록 폼 요청 - projectId: {}", projectId);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (!sessionUser.isExpert()) {
            return "redirect:/projects/" + projectId;
        }

        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.findProjectById(projectId));
        return "bid/bid-save";
    }

    // 2. 제안서 등록
    @PostMapping("/bids")
    public String save(BidRequestDTO.SaveDTO req, HttpSession session,
                       RedirectAttributes redirectAttrs) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        req.validate();
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        bidService.saveBid(req, member);
        redirectAttrs.addFlashAttribute("successMessage", "제안서가 성공적으로 제출되었습니다.");
        return "redirect:/projects/" + req.getProjectId();
    }

    // 3. 제안서 목록 조회 (의뢰인 시점)
    @GetMapping("/bids")
    public String list(Model model,
                       @RequestParam Long projectId,
                       @RequestParam(defaultValue = "0") int page,
                       HttpSession session) {
        log.info("의뢰인의 제안서 목록 조회 요청 - projectId: {}", projectId);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser != null) {
            model.addAttribute("sessionUser", sessionUser);
        }

        Member member = sessionUser != null ? memberRepository.findById(sessionUser.getId()).orElse(null) : null;
        Page<BidResponseDTO.ListDTO> bidPage = bidService.findByProjectId(projectId, member, page);

        model.addAttribute("bids", bidPage.getContent());
        model.addAttribute("projectId", projectId);
        model.addAttribute("projects", projectService.findProjectById(projectId));
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", bidPage.getTotalPages());
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < bidPage.getTotalPages() - 1);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        return "bid/bid-list";
    }

    // 4. 내 제안서 목록 조회 (전문가)
    @GetMapping("/bids/my-list")
    public String myList(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         HttpSession session) {
        log.info("전문가 본인의 제안서 목록 요청");
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        Page<BidResponseDTO.ListDTO> bidPage = bidService.findMyBids(member, page);

        model.addAttribute("bids", bidPage.getContent());
        model.addAttribute("bidCount", bidPage.getTotalElements());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", bidPage.getTotalPages());
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < bidPage.getTotalPages() - 1);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        return "bid/my-bids";
    }

    @GetMapping("/bids/my-orders")
    public String myOrders(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           HttpSession session) {
        log.info("전문가 주문 관리 요청");
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        Page<BidResponseDTO.OrderDTO> orderPage = bidService.findMyOrders(member, page);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("orderCount", orderPage.getTotalElements());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < orderPage.getTotalPages() - 1);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        if (sessionUser != null) {
            model.addAttribute("cancelRequests", cancelRequestService.getPendingForExpert(sessionUser.getId()));
        }
        return "bid/my-orders";
    }

    // 5. 제안서 수정 폼
    @GetMapping("/bids/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        log.info("제안서 수정 폼 요청 - bidId: {}", id);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        BidResponseDTO.DetailDTO bid = bidService.findBidById(id, member);
        model.addAttribute("bid", bid);
        return "bid/bid-update";
    }

    // 6. 제안서 수정 (JS 비동기)
    @PutMapping("/bids/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody BidRequestDTO.UpdateDTO req,
                                    HttpSession session) {
        log.info("제안서 수정 요청 - bidId: {}", id);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        req.validate();
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        bidService.updateBid(id, req, member);
        return ResponseEntity.ok().build();
    }

    // 7. 제안서 취소/삭제 (JS 비동기)
    @DeleteMapping("/bids/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        log.info("제안서 취소 요청 - bidId: {}", id);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        bidService.deleteBid(id, member);
        return ResponseEntity.ok().build();
    }

    // 8. 낙찰 처리 (의뢰인이 선택)
    @PostMapping("/bids/{id}/select")
    @ResponseBody
    public ResponseEntity<?> select(@PathVariable Long id, HttpSession session) {
        log.info("의뢰인의 제안서 낙찰 처리 요청 - bidId: {}", id);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        try {
            Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
            bidService.selectBid(id, member);
            return ResponseEntity.ok().build();
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 9. 제안서 보류 처리
    @PostMapping("/bids/{id}/hold")
    @ResponseBody
    public ResponseEntity<?> hold(@PathVariable Long id, HttpSession session) {
        log.info("제안서 보류 서비스 시작");
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        bidService.holdBid(id, member);
        return ResponseEntity.ok().build();
    }



    // 10. 제안서 거절 (의뢰인이 거절 - JS 비동기)
    @DeleteMapping("/bids/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> reject(@PathVariable Long id, HttpSession session) {
        log.info("제안서 거절 요청 - bidId: {}", id);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return ResponseEntity.status(401).build();
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        bidService.rejectBid(id, member);
        return ResponseEntity.ok().build();
    }

    // 11. 전문가 작업 완료 신고
    @PostMapping("/bids/{id}/complete")
    @ResponseBody
    public ResponseEntity<?> complete(@PathVariable Long id, HttpSession session) {
        log.info("전문가 작업 완료 신고 요청 - bidId: {}", id);
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return ResponseEntity.status(401).build();
        try {
            Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
            bidService.completeBid(id, member);
            return ResponseEntity.ok().build();
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 12. 작업 상태 변경 (전문가)
    @PatchMapping("/bids/{id}/work-status")
    @ResponseBody
    public ResponseEntity<?> updateWorkStatus(@PathVariable Long id,
                                               @RequestParam String status,
                                               HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return ResponseEntity.status(401).build();
        try {
            WorkStatus ws = WorkStatus.valueOf(status);
            Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
            bidService.updateWorkStatus(id, ws, member);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "잘못된 상태값입니다"));
        }
    }

    // 13. 작업물 파일 제출 (전문가)
    @PostMapping("/bids/{id}/submit-work")
    @ResponseBody
    public ResponseEntity<?> submitWork(@PathVariable Long id,
                                         @RequestParam(required = false) MultipartFile file,
                                         @RequestParam(required = false) String note,
                                         HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return ResponseEntity.status(401).build();
        try {
            Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
            bidService.submitWork(id, file, note, member);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
