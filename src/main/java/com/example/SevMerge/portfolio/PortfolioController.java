package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.expertprofile.ExpertProfileService;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ExpertProfileService expertProfileService;

    @GetMapping("/portfolios")
    public String showPortfolios(HttpSession session,
                                 Model model) {

        ExpertProfile expert = (ExpertProfile) session.getAttribute(Define.SESSION_USER);

        List<PortfolioResponse.ListDTO> portfolios = portfolioService.findByMemberId(expert.getMember().getId());
        model.addAttribute("portfolios", portfolios);
        return "portfolio/portfolio-list";
    }

    @GetMapping("/portfolios/{portfolioId}")
    public String detailPortfolio(@PathVariable(name = "portfolioId") Long portfolioId,
                                  HttpSession session,
                                  Model model) {

        ExpertProfile expert = (ExpertProfile) session.getAttribute(Define.SESSION_USER);

        PortfolioResponse.DetailDTO detailPortfolio = portfolioService.findPortfolio(portfolioId);

        model.addAttribute("expert", expert);
        model.addAttribute("portfolio", detailPortfolio);

        return "portfolio/portfolio-detail";
    }

    @GetMapping("/portfolios/save")
    public String savePortfoliosPage(HttpSession session, Model model) {

        Member member = (Member) session.getAttribute(Define.SESSION_USER);

        ExpertProfileResponse expertProfileEntity = expertProfileService.getByMemberId(member.getId());

        model.addAttribute("expertProfile", expertProfileEntity);

        return "portfolio/portfolio-save";
    }

    @PostMapping("/portfolios/save")
    public String savePortfolios(PortfolioRequest.SaveDTO saveDTO) {

        portfolioService.save(saveDTO);

        return "redirect:/portfolios";
    }




    @GetMapping("/portfolios/{portfolioId}/edit")
    public String updatePortfoliosPage(@PathVariable(name = "portfolioId") Long portfolioId , Model model) {

        PortfolioResponse.UpdateDTO portfolio = portfolioService.updatePage(portfolioId);

        model.addAttribute("portfolio",portfolio);
        return "portfolio/portfolio-update";
    }

    @PostMapping("/portfolios/{portfolioId}/update" )
    public String updatePortfolios(@PathVariable(name = "portfolioId") Long portfolioId,PortfolioRequest.UpdateDTO updateDTO) {

        portfolioService.update(portfolioId,updateDTO);

        return "redirect:/portfolios";
    }


    @PostMapping("/portfolios/{portfolioId}/delete")
    public String deletePortfolios(@PathVariable(name = "portfolioId") Long portfolioId) {

        portfolioService.delete(portfolioId);

        return "redirect:/portfolios";
    }

}
