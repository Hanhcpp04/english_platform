package com.englishplatform.controller;

import com.englishplatform.model.Vocabulary;
import com.englishplatform.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vocabulary")
public class VocabularyController {

    @Autowired
    private VocabularyService vocabularyService;

    @GetMapping
    public String vocabularyList(Model model, 
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String difficulty,
                               @RequestParam(required = false) String wordType) {
        
        List<Vocabulary> vocabularyList;
        
        if (search != null && !search.trim().isEmpty()) {
            vocabularyList = vocabularyService.searchVocabulary(search.trim());
        } else if (difficulty != null && !difficulty.isEmpty()) {
            vocabularyList = vocabularyService.findByDifficulty(Vocabulary.DifficultyLevel.valueOf(difficulty));
        } else if (wordType != null && !wordType.isEmpty()) {
            vocabularyList = vocabularyService.findByWordType(Vocabulary.WordType.valueOf(wordType));
        } else {
            vocabularyList = vocabularyService.findAllVocabulary();
        }

        model.addAttribute("title", "Vocabulary - English Learning Platform");
        model.addAttribute("vocabularyList", vocabularyList);
        model.addAttribute("search", search);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("selectedWordType", wordType);
        model.addAttribute("difficulties", Vocabulary.DifficultyLevel.values());
        model.addAttribute("wordTypes", Vocabulary.WordType.values());

        return "vocabulary/list";
    }

    @GetMapping("/{id}")
    public String vocabularyDetail(@PathVariable Long id, Model model) {
        Optional<Vocabulary> vocabularyOpt = vocabularyService.findById(id);
        
        if (vocabularyOpt.isEmpty()) {
            return "redirect:/vocabulary";
        }

        model.addAttribute("title", "Vocabulary Detail - English Learning Platform");
        model.addAttribute("vocabulary", vocabularyOpt.get());

        return "vocabulary/detail";
    }

    @GetMapping("/random")
    public String randomVocabulary(Model model, @RequestParam(defaultValue = "10") int limit) {
        List<Vocabulary> randomWords = vocabularyService.getRandomWords(limit);
        
        model.addAttribute("title", "Random Vocabulary - English Learning Platform");
        model.addAttribute("vocabularyList", randomWords);
        model.addAttribute("isRandom", true);

        return "vocabulary/list";
    }
}