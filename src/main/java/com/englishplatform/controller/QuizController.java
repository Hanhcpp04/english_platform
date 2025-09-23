package com.englishplatform.controller;

import com.englishplatform.model.Quiz;
import com.englishplatform.model.QuizQuestion;
import com.englishplatform.model.QuizResult;
import com.englishplatform.model.User;
import com.englishplatform.service.QuizService;
import com.englishplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String quizList(Model model, @RequestParam(required = false) String difficulty) {
        List<Quiz> quizzes;
        
        if (difficulty != null && !difficulty.isEmpty()) {
            quizzes = quizService.findQuizzesByDifficulty(Quiz.DifficultyLevel.valueOf(difficulty));
        } else {
            quizzes = quizService.findAllQuizzes();
        }

        model.addAttribute("title", "Quizzes - English Learning Platform");
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("difficulties", Quiz.DifficultyLevel.values());

        return "quiz/list";
    }

    @GetMapping("/{id}")
    public String quizDetail(@PathVariable Long id, Model model) {
        Optional<Quiz> quizOpt = quizService.findQuizById(id);
        
        if (quizOpt.isEmpty()) {
            return "redirect:/quiz";
        }

        Quiz quiz = quizOpt.get();
        List<QuizQuestion> questions = quizService.findQuestionsByQuizId(id);

        model.addAttribute("title", quiz.getTitle() + " - English Learning Platform");
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);

        return "quiz/detail";
    }

    @GetMapping("/{id}/take")
    public String takeQuiz(@PathVariable Long id, Model model) {
        Optional<Quiz> quizOpt = quizService.findQuizById(id);
        
        if (quizOpt.isEmpty()) {
            return "redirect:/quiz";
        }

        Quiz quiz = quizOpt.get();
        List<QuizQuestion> questions = quizService.findQuestionsByQuizId(id);

        model.addAttribute("title", "Take Quiz: " + quiz.getTitle());
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);

        return "quiz/take";
    }

    @PostMapping("/{id}/submit")
    public String submitQuiz(@PathVariable Long id, 
                           @RequestParam List<String> answers,
                           RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        try {
            QuizResult result = quizService.submitQuiz(userOpt.get(), id, answers);
            redirectAttributes.addFlashAttribute("result", result);
            return "redirect:/quiz/" + id + "/result";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/quiz/" + id;
        }
    }

    @GetMapping("/{id}/result")
    public String quizResult(@PathVariable Long id, Model model) {
        // Get the latest result for this user and quiz
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        List<QuizResult> results = quizService.findResultsByUserId(user.getId());
        
        // Find the latest result for this quiz
        Optional<QuizResult> latestResult = results.stream()
            .filter(r -> r.getQuiz().getId().equals(id))
            .findFirst();

        if (latestResult.isEmpty()) {
            return "redirect:/quiz/" + id;
        }

        model.addAttribute("title", "Quiz Result - English Learning Platform");
        model.addAttribute("result", latestResult.get());

        return "quiz/result";
    }

    @GetMapping("/results")
    public String myResults(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        List<QuizResult> results = quizService.findResultsByUserId(user.getId());

        model.addAttribute("title", "My Quiz Results - English Learning Platform");
        model.addAttribute("results", results);

        return "quiz/my-results";
    }
}