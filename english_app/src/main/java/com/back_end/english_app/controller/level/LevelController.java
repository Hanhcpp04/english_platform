package com.back_end.english_app.controller.level;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.entity.LevelEntity;
import com.back_end.english_app.repository.LevelRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/level")
@RequiredArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class LevelController {
    final LevelRepository levelRepository;
    @GetMapping("/getAll")
    public APIResponse<List<LevelEntity>> getAlḷ(){
        List<LevelEntity> lst = levelRepository.findAll();
        return APIResponse.<List<LevelEntity>>builder()
                .result(lst)
                .build();
    }
}
