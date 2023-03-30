package com.blogs.learningblog.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {
    @GetMapping("/logout")
    public String logoutProfile(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("id");
        session.removeAttribute("user");

        return "redirect:/auth";
    }
}
