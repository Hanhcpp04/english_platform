package com.englishplatform.controller;

import com.englishplatform.model.User;
import com.englishplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Login - English Learning Platform");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Register - English Learning Platform");
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("title", "Register - English Learning Platform");
            return "auth/register";
        }

        try {
            userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword(),
                                   user.getFirstName(), user.getLastName());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! Please login with your credentials.");
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            model.addAttribute("title", "Register - English Learning Platform");
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
}