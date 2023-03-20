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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Controller
public class ProfileController {

    @Autowired
    UserRepository userRepository;
    @GetMapping("/profile/{id}")
    public String showProfile(Model model, @PathVariable String id, HttpServletRequest request, @RequestParam Map<String, String> data) {

        HttpSession session = request.getSession();

        if (session.getAttribute("id") != null) {
            if (Objects.equals(session.getAttribute("id").toString(), id)) {
                model.addAttribute("id", id);

                User user = userRepository.findById(Long.valueOf(id)).get();
                model.addAttribute("email", user.getEmail());
                model.addAttribute("username", user.getUsername());
                model.addAttribute("imgName", user.getImgName());

                return "profilePage";
            }
            return "redirect:/profile/" + session.getAttribute("id");
        }
        return "redirect:/auth";
    }

    @PostMapping("/profile/{id}")
    public String changeProfile(Model model, @PathVariable String id, HttpServletRequest request, @RequestParam Map<String, String> data, @RequestParam("avatar") MultipartFile file) {
        String username = data.get("username");
        User user = userRepository.findById(Long.valueOf(id)).get();

        String date = String.valueOf(LocalDateTime.now().getSecond());


        if (!file.isEmpty()) {
            try {

                String saveDirectory = "C:/Users/я.DESKTOP-CN63JQ9/Documents/Study/SpringBoot/learning-blog/src/main/resources/static/img/avatars/";

                if (!user.getImgName().equals("avatar_logo.jpg"))
                    Files.delete(Path.of(saveDirectory + user.getImgName()));

                File dir = new File(saveDirectory);

                String filePath = saveDirectory + user.getId() + date + ".jpg";
                File uploadedFile = new File(filePath);
                file.transferTo(uploadedFile);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        user.setUsername(username);
        user.setImgName(user.getId() + String.valueOf(date) + ".jpg");
        userRepository.save(user);

        model.addAttribute("email", user.getEmail());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("imgName", user.getImgName());
        return "profilePage";
    }
}
