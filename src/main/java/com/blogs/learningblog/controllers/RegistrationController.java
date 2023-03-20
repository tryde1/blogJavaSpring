package com.blogs.learningblog.controllers;

import com.blogs.learningblog.models.User;
import com.blogs.learningblog.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import javax.print.attribute.standard.JobKOctets;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/register")
    public String register(Model model) {
        return "registerPage";
    }

    @PostMapping(value = "/register")
    public String showProfile (Model model, @RequestParam Map<String, String> data, HttpServletRequest request) {
        String username = data.get("username");
        String email = data.get("email");
        String password = data.get("password");
        String confPassword = data.get("confirmPassword");
        String error;

        if (Objects.requireNonNull(getUsernames()).contains(username)) {
            error = "existingUsername";
            model.addAttribute("error", error);
            return "registerPage";
        }
        else if (Objects.requireNonNull(getEmails()).contains(email)) {
            error = "existingEmail";
            model.addAttribute("error", error);
            return "registerPage";
        }
        else if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confPassword.isEmpty()) {
            error = "emptyInput";
            model.addAttribute("error", error);
            return "registerPage";
        } else if (!password.equals(confPassword)) {
            error = "invalidPasswordConfirmation";
            model.addAttribute("error", error);
            return "registerPage";
        } else {
            User user = new User();
            fillUsersData(username, email, password, user);
            userRepository.save(user);

            HttpSession session = request.getSession();
            session.setAttribute("id", user.getId());

            return "redirect:/profile/" + user.getId();
        }
    }

    private User fillUsersData(String username, String email, String password, User user) {
        LocalDateTime date = LocalDateTime.now();

        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        user.setImgName("avatar_logo.jpg");
        user.setCreateDate(String.valueOf(date));
        user.setUpdateDate(String.valueOf(date));

        return user;
    }

    private List<String> getEmails() {
        Iterable<User> users = userRepository.findAll();
        List<String> emails = new ArrayList<>();

        for (User user : users) {
            emails.add(user.getEmail());
        }

        return emails;
    }

    private List<String> getUsernames() {
        Iterable<User> users = userRepository.findAll();
        List<String> usernames = new ArrayList<>();

        for (User user : users) {
            usernames.add(user.getUsername());
        }

        return usernames;
    }
}
