package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/portfolios")
    public String showPortfolios(HttpSession session) {

        ExpertProfile expert = (ExpertProfile) session.getAttribute(Define.SESSION_USER);

        List<PortfolioResponse.ListDTO> portfolios = portfolioService.findByMemberId(expert.getMember().getId());
        return null;
    }

    @GetMapping("/portfolios/deatil")
    public String detailPortfolio() {

        return null;
    }

    @GetMapping("/portfolios/save")
    public String savePortfoliosPage() {

        return null;
    }

    @PostMapping("/portfolios/save")
    public String savePortfolios() {

        return null;
    }

    @GetMapping("/portfolios/update")
    public String updatePortfoliosPage() {

        return null;
    }

    @PostMapping("/portfolios/update")
    public String updatePortfolios() {

        return null;
    }

    @PostMapping("/portfolios/delete")
    public String deletePortfolios() {

        return null;
    }

}
