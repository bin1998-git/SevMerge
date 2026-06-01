package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/portfolios")
    public String showPortfolios(HttpSession session,
                                 Model model) {

        ExpertProfile expert = (ExpertProfile) session.getAttribute(Define.SESSION_USER);

        List<PortfolioResponse.ListDTO> portfolios = portfolioService.findByMemberId(expert.getMember().getId());
        model.addAttribute("portfolios",portfolios);
        return "portfolio/portfolio-list";
    }

    @GetMapping("/portfolios/{portfolioId}")
    public String detailPortfolio(@PathVariable(name="portfolioId") Long portfolioId,
                                  HttpSession session,
                                  Model model) {

        ExpertProfile expert = (ExpertProfile) session.getAttribute(Define.SESSION_USER);

        PortfolioResponse.DetailDTO detailPortfolio = portfolioService.findPortfolio(portfolioId);

        model.addAttribute("expert",expert);
        model.addAttribute("portfolio",detailPortfolio);

        return "portfolio/portfolio-detail";
    }

    @GetMapping("/portfolios/save")
    public String savePortfoliosPage() {

        return "portfolio/portfolio-save";
    }

    @PostMapping("/portfolios/save")
    public String savePortfolios() {

        return "redirect:/portfolio/portfolio-list";
    }

    @GetMapping("/portfolios/update")
    public String updatePortfoliosPage() {

        return "portfolio/portfolio-update";
    }

    @PostMapping("/portfolios/update")
    public String updatePortfolios() {

        return "redirect:portfolio/portfolio-list";
    }

    @PostMapping("/portfolios/delete")
    public String deletePortfolios() {

        return null;
    }

}
