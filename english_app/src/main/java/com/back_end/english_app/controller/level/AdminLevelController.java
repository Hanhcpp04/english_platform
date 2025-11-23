package com.back_end.english_app.controller.level;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.level.LevelRequest;
import com.back_end.english_app.dto.request.level.LevelUpdateRequest;
import com.back_end.english_app.entity.LevelEntity;
import com.back_end.english_app.service.AdminLevelService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
public class AdminLevelController {
    public final AdminLevelService adminLevelService;

    @GetMapping()
    public APIResponse<List<LevelEntity>> getAllLevels(){
        return adminLevelService.getAllLevels();
    }

    @PostMapping()
    public APIResponse<?> addNewLevel(@RequestBody LevelRequest request){
        return adminLevelService.addNewLevel(request);
    }

    @PutMapping("/{levelNumber}")
    public APIResponse<?> updateLevel(@PathVariable Integer levelNumber, @RequestBody LevelUpdateRequest request){
        return adminLevelService.updateLevel(levelNumber, request);
    }
}
