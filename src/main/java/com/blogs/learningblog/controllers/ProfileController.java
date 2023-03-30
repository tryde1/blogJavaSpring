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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Map;
import java.util.Objects;


@Controller
public class ProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilesStorageServiceImpl storageService;
    @GetMapping("/profile/{id}")
    public String showProfile(Model model, @PathVariable String id, HttpServletRequest request, @RequestParam Map<String, String> data) {

        HttpSession session = request.getSession();

        if (session.getAttribute("id") != null) {
            if (Objects.equals(session.getAttribute("id").toString(), id)) {
                model.addAttribute("id", id);

                User user = userRepository.findById(Long.valueOf(id)).get();

                String url = MvcUriComponentsBuilder
                        .fromMethodName(ProfileController.class, "getImage", user.getImgName().toString(), id).build().toString();

                user.setUrl(url);
                userRepository.save(user);

                model.addAttribute("email", user.getEmail());
                model.addAttribute("username", user.getUsername());
                model.addAttribute("url", url);
                session.setAttribute("user", user);

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

        storageService.delete(String.valueOf(user.getId()));

        storageService.save(file, String.valueOf(user.getId()));

        user.setUsername(username);

        user.setImgName(String.valueOf(user.getId()));

        userRepository.save(user);

        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("/profile/{id}/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename, @PathVariable String id) {
        Resource file = storageService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
