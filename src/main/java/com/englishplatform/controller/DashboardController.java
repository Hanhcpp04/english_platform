package com.englishplatform.controller;

import com.englishplatform.model.User;
import com.englishplatform.model.UserProgress;
import com.englishplatform.service.QuizService;
import com.englishplatform.service.UserProgressService;
import com.englishplatform.service.UserService;
import com.englishplatform.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProgressService userProgressService;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private QuizService quizService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Optional<UserProgress> progressOpt = userProgressService.findByUserId(user.getId());
        
        model.addAttribute("title", "Dashboard - English Learning Platform");
        model.addAttribute("user", user);
        
        if (progressOpt.isPresent()) {
            UserProgress progress = progressOpt.get();
            model.addAttribute("progress", progress);
            model.addAttribute("totalVocabulary", vocabularyService.getVocabularyCount());
            model.addAttribute("averageScore", quizService.getAverageScoreByUserId(user.getId()));
        }

        return "dashboard/index";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Optional<UserProgress> progressOpt = userProgressService.findByUserId(user.getId());
        
        model.addAttribute("title", "Profile - English Learning Platform");
        model.addAttribute("user", user);
        if (progressOpt.isPresent()) {
            model.addAttribute("progress", progressOpt.get());
        }

        return "dashboard/profile";
    }
}