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
@RequestMapping("/admin-levels")
@RequiredArgsConstructor
public class AdminLevelController {
    public final AdminLevelService adminLevelService;

    @GetMapping("/getAll")
    public APIResponse<List<LevelEntity>> getAllLevels(){
        return adminLevelService.getAllLevels();
    }

    @PostMapping("/create")
    public APIResponse<?> addNewLevel(@RequestBody LevelRequest request){
        return adminLevelService.addNewLevel(request);
    }

    @PutMapping("/update/{levelNumber}")
    public APIResponse<?> updateLevel(@PathVariable Integer levelNumber, @RequestBody LevelUpdateRequest request){
        return adminLevelService.updateLevel(levelNumber, request);
    }
}
