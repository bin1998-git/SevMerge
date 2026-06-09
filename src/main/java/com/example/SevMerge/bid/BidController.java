package com.example.SevMerge.bid;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final ProjectService projectService;

    // 1. 제안서 작성 폼
    @GetMapping("/bid/save-form")
    public String saveForm(@RequestParam Long projectId, Model model, HttpSession session) {
        log.info("제안서 등록 폼 요청 - projectId: {}", projectId);
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        if (!sessionUser.isExpert()) {
            return "redirect:/projects/" + projectId + "/detail";
        }

        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.findProjectById(projectId));
        return "bid/bid-save";
    }

    // 2. 제안서 등록
    @PostMapping("/bid/save")
    public String save(BidRequestDTO.SaveDTO req, HttpSession session) {
        log.info("제안서 등록 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        // 유효성 검사
        req.validate();
        bidService.saveBid(req, sessionUser);

        return "redirect:/projects/" + req.getProjectId() + "/detail";
    }

    // 3. 제안서 목록 조회 (의뢰인 시점)
    @GetMapping("/bid/list")
    public String list(Model model, @RequestParam Long projectId, HttpSession session) {
        log.info("의뢰인의 제안서 목록 조회 요청 - projectId: {}", projectId);
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        model.addAttribute("bids", bidService.findByProjectId(projectId, sessionUser));
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.findProjectById(projectId));
        return "bid/bid-list";
    }

    // 4. 내 제안서 목록 조회 (전문가)
    @GetMapping("/bid/my-list")
    public String myList(Model model, HttpSession session) {
        log.info("전문가 본인의 제안서 목록 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        List<BidResponseDTO.ListDTO> bids = bidService.findMyBids(sessionUser);
        model.addAttribute("bids", bids);
        model.addAttribute("bidCount", bids.size());
        return "bid/my-bids";
    }

    // 4-1. 주문 관리 (전문가 — 낙찰된 건만)
    @GetMapping("/bid/my-orders")
    public String myOrders(Model model, HttpSession session) {
        log.info("전문가 주문 관리 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        List<BidResponseDTO.OrderDTO> orders = bidService.findMyOrders(sessionUser);
        model.addAttribute("orders", orders);
        model.addAttribute("orderCount", orders.size());
        return "bid/my-orders";
    }

    // 5. 제안서 수정 폼
    @GetMapping("/bid/{id}/update-form")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        log.info("제안서 수정 폼 요청 - bidId: {}", id);
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        BidResponseDTO.DetailDTO bid = bidService.findBidById(id, sessionUser);
        model.addAttribute("bid", bid);
        return "bid/bid-update";
    }

    // 6. 제안서 수정 (JS 비동기)
    @PutMapping("/bid/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody BidRequestDTO.UpdateDTO req,
                                    HttpSession session) {
        log.info("제안서 수정 요청 - bidId: {}", id);
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        req.validate();
        bidService.updateBid(id, req, sessionUser);
        return ResponseEntity.ok().build();
    }

    // 7. 제안서 취소/삭제 (JS 비동기)
    @DeleteMapping("/bid/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        log.info("제안서 취소 요청 - bidId: {}", id);
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        bidService.deleteBid(id, sessionUser);
        return ResponseEntity.ok().build();
    }

    // 8. 낙찰 처리 (의뢰인이 선택)
    @PostMapping("/bid/{id}/select")
    @ResponseBody
    public ResponseEntity<?> select(@PathVariable Long id, HttpSession session) {
        log.info("의뢰인의 제안서 낙찰 처리 요청 - bidId: {}", id);
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        bidService.selectBid(id, sessionUser);
        return ResponseEntity.ok().build();
    }
}
