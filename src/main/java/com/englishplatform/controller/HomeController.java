package com.englishplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Welcome to English Learning Platform");
        return "index";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return "redirect:/";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About English Learning Platform");
        return "about";
    }
}