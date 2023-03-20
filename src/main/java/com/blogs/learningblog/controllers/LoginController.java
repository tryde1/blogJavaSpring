package com.blogs.learningblog.controllers;

import com.blogs.learningblog.models.User;
import com.blogs.learningblog.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/auth")
    public String showAuth(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("id") != null) {
            return "redirect:/profile/" + session.getAttribute("id");
        }
        return "authPage";
    }

    @PostMapping("/auth")
    public String authUser(Model model, @RequestParam Map<String, String> data, HttpServletRequest request) {
        String email = data.get("email");
        String password = data.get("password");

        HttpSession session = request.getSession();

        Iterable<User> users = userRepository.findAll();

        for (User user: users) {
            if (user.getPassword().equals(password) && user.getEmail().equals(email)) {

                session.setAttribute("id", user.getId());

                return "redirect:/profile/" + user.getId();
            }
        }

        model.addAttribute("error", "invalidPassword");
        return "authPage";
    }
}
