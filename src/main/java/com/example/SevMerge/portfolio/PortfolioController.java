package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.expertprofile.ExpertProfileService;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ExpertProfileService expertProfileService;
    private final MemberService memberService;

    // 리스트
    @GetMapping("/portfolios")
    public String showPortfolios(HttpSession session,
                                 Model model, @RequestParam("expertId") Long expertId, @RequestParam(defaultValue = "1") int page) {

        Member member = (Member) session.getAttribute(Define.SESSION_USER);

        Page<PortfolioResponse.ListDTO> portfolios = portfolioService.findByMemberId(expertId,page);

        ExpertProfileResponse expertProfile = expertProfileService.getByMemberId(expertId); // 멤버 아이디

        model.addAttribute("portfolios", portfolios);
        model.addAttribute("expertProfile", expertProfile);
        model.addAttribute("totalPages",portfolios.getTotalPages());
        model.addAttribute("currentPage",page);
        model.addAttribute("hasPrev",page > 1 ? page - 1 : null );
        model.addAttribute("hasNext", page < portfolios.getTotalPages() ? page + 1 : null );
        model.addAttribute("isOwner", member != null && member.getId().equals(expertId));

        return "portfolio/portfolio-list";
    }


    @GetMapping("/portfolios/{portfolioId}")
    public String detailPortfolio(@PathVariable(name = "portfolioId") Long portfolioId,
                                  HttpSession session,
                                  Model model) {

        Member expert = (Member) session.getAttribute(Define.SESSION_USER);

        PortfolioResponse.DetailDTO detailPortfolio = portfolioService.findPortfolio(portfolioId);

        model.addAttribute("expert", expert);
        model.addAttribute("portfolio", detailPortfolio);
        model.addAttribute("isOwner", expert != null && expert.getId().equals(detailPortfolio.getExpertId()));

        return "portfolio/portfolio-detail";
    }

    @GetMapping("/portfolios/save")
    public String savePortfoliosPage(HttpSession session, Model model, @RequestParam("expertId") Long expertId) {
        Member member = (Member) session.getAttribute(Define.SESSION_USER);
        if (member == null) {
            return "redirect:/login";
        }
        ExpertProfileResponse expertProfileEntity = expertProfileService.getByMemberId(member.getId());

        model.addAttribute("expertProfile", expertProfileEntity);

        return "portfolio/portfolio-save";
    }

    @PostMapping("/portfolios/save")
    public String savePortfolios(PortfolioRequest.SaveDTO saveDTO, HttpSession session) {

        Member member = (Member) session.getAttribute(Define.SESSION_USER);
        if (member == null) {
            return "redirect:/login";
        }
        if (member.isExpert() == false) {
            throw new BadRequestException("전문가만 포트폴리오를 작성할수 있습니다.");
        }
        portfolioService.save(saveDTO,member.getId());

        return "redirect:/portfolios?expertId=" + saveDTO.getExpertId();
    }


    @GetMapping("/portfolios/{portfolioId}/edit")
    public String updatePortfoliosPage(@PathVariable(name = "portfolioId") Long portfolioId, Model model, HttpSession session) {

        Member member = (Member) session.getAttribute(Define.SESSION_USER);
        if (member == null) {
            return "redirect:/login";
        }
        PortfolioResponse.DetailDTO detailPortfolio = portfolioService.findPortfolio(portfolioId);

        model.addAttribute("portfolio", detailPortfolio);

        return "portfolio/portfolio-update";
    }

    @PostMapping("/portfolios/{portfolioId}/update")
    public String updatePortfolios(@PathVariable(name = "portfolioId") Long portfolioId, PortfolioRequest.UpdateDTO updateDTO,
                                   @RequestParam(name = "expertId") Long expertId, HttpSession session
    ) {
        Member member = (Member) session.getAttribute(Define.SESSION_USER);
        if (member == null) {
            return "redirect:/login";
        }
        portfolioService.update(portfolioId, updateDTO, member.getId());

        return "redirect:/portfolios?expertId=" + expertId;
    }


    @PostMapping("/portfolios/{portfolioId}/delete")
    public String deletePortfolios(@PathVariable(name = "portfolioId") Long portfolioId, @RequestParam(name = "expertId") Long expertId, HttpSession session) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            return "redirect:/login";
        }
        if (sessionMember.getId() != expertId) {
            throw new BadRequestException("삭제 권한이 없습니다.");
        }

        portfolioService.delete(portfolioId);

        return "redirect:/portfolios?expertId=" + expertId;
    }




}
