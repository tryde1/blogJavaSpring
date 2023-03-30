package com.blogs.learningblog.controllers;

import com.blogs.learningblog.models.Article;
import com.blogs.learningblog.models.User;
import com.blogs.learningblog.repos.ArticleRepository;
import com.blogs.learningblog.repos.UserRepository;
import com.sun.jdi.LongValue;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/article")
    public String showArticlePage(Model model, HttpServletRequest request) {

        Iterable<Article> articles = articleRepository.findAll();

        HttpSession session = request.getSession();

        model.addAttribute("articles", articles);

        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            model.addAttribute("image", user.getUrl());

            return switch (user.getPermissions()) {
                case "user" -> "articlesUserPage";
                default -> "articlesModeratorPage";
            };
        }
        else {
            return "articlesUnAuthPage";
        }
    }

    @PostMapping("/article/delete/{id}")
    public String deleteArticle (Model model, @PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println(user);

        if (user != null) {
            if (Objects.equals(user.getPermissions(), "admin") || Objects.equals(user.getPermissions(), "moderator")) {
                articleRepository.deleteById(Long.valueOf(id));
            }
        }
        return "redirect:/article";
    }

    @PostMapping("/article/hide/{id}")
    public String hideArticle (Model model, @PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            if (Objects.equals(user.getPermissions(), "admin") || Objects.equals(user.getPermissions(), "moderator")) {
                Article article = articleRepository.findById(Long.valueOf(id)).get();

                article.setHidden(true);
                articleRepository.save(article);
            }
        }
        return "redirect:/article";
    }

    @PostMapping("/article/show/{id}")
    public String showArticle (Model model, @PathVariable String id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            if (Objects.equals(user.getPermissions(), "admin") || Objects.equals(user.getPermissions(), "moderator")) {
                Article article = articleRepository.findById(Long.valueOf(id)).get();

                article.setHidden(false);
                articleRepository.save(article);
            }
        }
        return "redirect:/article";
    }
}
