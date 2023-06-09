package com.blogs.learningblog.controllers;

import com.blogs.learningblog.models.User;
import com.blogs.learningblog.repos.UserRepository;
import com.blogs.learningblog.service.FilesStorageServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    FilesStorageServiceImpl storageService;

    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (session.getAttribute("user") != null) {
            return "redirect:/profile/" + user.getId();
        }
        return "registerPage";
    }

    @PostMapping(value = "/register")
    public String showProfile (Model model, @RequestParam Map<String, String> data, HttpServletRequest request) {

        String username = data.get("username");

        String email = data.get("email");
        String error;

        if (Objects.requireNonNull(getUsernames()).contains(username)) {
            error = "existingUsername";
            model.addAttribute("error", error);
        }
        else if (Objects.requireNonNull(getEmails()).contains(email)) {
            error = "existingEmail";
            model.addAttribute("error", error);
        }
        else if (data.get("password").isEmpty() || data.get("confirmPassword").isEmpty() || data.get("email").isEmpty() || data.get("username").isEmpty()) {
            error = "emptyInput";
            model.addAttribute("error", error);
        } else if (data.get("password").length() <= 6) {
            error = "invalidPassword";
            model.addAttribute("error", error);
        }
        else if (!data.get("password").equals(data.get("confirmPassword"))) {
            error = "invalidPasswordConfirmation";
            model.addAttribute("error", error);
        } else {
            User user = new User();
            fillUsersData(username, email, DigestUtils.md5DigestAsHex(data.get("password").getBytes()), user);
            userRepository.save(user);

            HttpSession session = request.getSession();
            session.setAttribute("id", user.getId());

            return "redirect:/profile/" + user.getId();
        }
        return "registerPage";
    }

    private User fillUsersData(String username, String email, String password, User user) {
        LocalDateTime date = LocalDateTime.now();

        user.setEmail(email);
        user.setPassword(password.toString());
        user.setPermissions("user");
        user.setUsername(username);
        user.setImgName("avatar_logo");
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
