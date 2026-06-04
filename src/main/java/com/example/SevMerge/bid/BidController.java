package com.example.SevMerge.bid;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final ProjectService projectService;

    // 제안서 작성폼
    @GetMapping("/bid/save-form")
    public String saveForm(@RequestParam Long projectId, Model model) {
        log.info("제안서 등록 폼 요청");
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.findProjectById(projectId));

        return "bid/bid-save";
    }

    // 제안서 작성
    @PostMapping("/bid/save")
    public String save(BidRequestDTO.SaveDTO req, HttpSession session) {
        log.info("제안서 등록 요청");
        req.validate();

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        bidService.saveBid(req, sessionUser);
        return "redirect:/projects/" + req.getProjectId() + "/detail";

    }

    // 제안서 목록 조회 (의뢰인)
    @GetMapping("/bid/list")
    public String list(Model model, @RequestParam Long projectId, HttpSession session) {
        log.info("제안서 목록 조회 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("bids", bidService.findByProjectId(projectId, sessionUser));
        model.addAttribute("projectId", projectId);
        model.addAttribute("projects", projectService.findProjectById(projectId));
        return "bid/bid-list";
    }

    // 제안서 목록 조회(전문가)
    @GetMapping("/bid/my-list")
    public String myList(Model model, HttpSession session) {
        log.info("내 제안서 목록 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("bids", bidService.findMyBids(sessionUser));
        return "bid/bid-my-list";
    }

    // 제안서 수정 폼
    @GetMapping("/bid/{id}/update-form")
    public String updateForm(@PathVariable Long id, Model model) {
        log.info("제안서 수정 폼 요청");
        return "bid/bid-update";
    }

    // 제안서 수정
    @PostMapping("/bid/{id}/update")
    public String update(@PathVariable Long id, BidRequestDTO.UpdateDTO req,
                         HttpSession session) {
        log.info("제안서 수정 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        bidService.updateBid(id, req, sessionUser);
        return "redirect:/bid/my-list";

    }

    // 제안서 취소
    @PostMapping("/bid/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        log.info("제안서 취소 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        bidService.deleteBid(id, sessionUser);
        return "redirect:/bid/my-list";
    }

    // 낙찰 처리
    @PostMapping("/bid/{id}/select")
    public String select(@PathVariable Long id, HttpSession session) {
        log.info("낙찰 처리 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        bidService.selectBid(id, sessionUser);
        return "redirect:/projects/list";
    }

}
