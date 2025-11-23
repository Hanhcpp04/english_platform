package com.back_end.english_app.service;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.level.LevelRequest;
import com.back_end.english_app.dto.request.level.LevelUpdateRequest;
import com.back_end.english_app.entity.LevelEntity;
import com.back_end.english_app.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminLevelService {
    private final LevelRepository levelRepository;

    //lấy danh sách level
    public APIResponse<List<LevelEntity>> getAllLevels(){
        List<LevelEntity> levels = levelRepository.findAll();
        return APIResponse.success(levels);
    }

    //thêm level mới
    public APIResponse<?> addNewLevel(LevelRequest request){
        LevelEntity level = new LevelEntity();

        Optional<LevelEntity> optHighestLevel = levelRepository.findTopByOrderByLevelNumberDesc();
        LevelEntity highestLevel = optHighestLevel.get();

        level.setLevelNumber(highestLevel.getLevelNumber() + 1);

        //kiểm tra min xp và max xp
        if (highestLevel.getMaxXp() != null){
            if(request.getMinXp() <= highestLevel.getMaxXp()){
                return APIResponse.error("min xp phải lớn hơn max xp của level cao nhất");
            }
        }
        else{
            //nếu max xp của level cao nhất hiện tại = null thì gán = min xp - 1
            if(request.getMinXp() <= highestLevel.getMinXp() + 1){
                return APIResponse.error("min xp phải lớn hơn min xp của level cao nhất ít nhất 2 đơn vị");
            }
            highestLevel.setMaxXp(request.getMinXp()-1);
        }
        level.setMinXp(request.getMinXp());

        if (request.getMaxXp() != null && request.getMinXp() >= request.getMaxXp()) {
            return APIResponse.error("xp không hợp lệ");
        }
        level.setMaxXp(request.getMaxXp());
        level.setLevelName(request.getLevelName());
        level.setDescription(request.getDescription());

        levelRepository.save(highestLevel);
        levelRepository.save(level);
        return  APIResponse.success(level);
    }

    //update tên và mô tả của level
    public APIResponse<?> updateLevel(Integer id, LevelUpdateRequest request) {
        Optional<LevelEntity> level = levelRepository.findByLevelNumber(id);
        if (level.isEmpty()) {
            return APIResponse.error("Level không tồn tại");
        }

        LevelEntity lv = level.get();

        if (request.getLevelName() != null)
            lv.setLevelName(request.getLevelName());

        if (request.getDescription() != null)
            lv.setDescription(request.getDescription());

        levelRepository.save(lv);

        return APIResponse.success("Cập nhật level thành công");
    }
}
