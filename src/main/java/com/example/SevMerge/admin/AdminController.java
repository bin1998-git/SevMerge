package com.example.SevMerge.admin;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String dashboardPage(HttpSession session, Model model) {
        User sessionUser = (User)  session.getAttribute("sessionUser");
        model.addAttribute("admin", sessionUser);
        return "admin";
    }
}
