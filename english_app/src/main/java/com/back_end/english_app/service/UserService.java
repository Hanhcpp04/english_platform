package com.back_end.english_app.service;

import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
    
    @Transactional
    public void addXP(Long userId, int xpAmount) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int oldXP = user.getTotalXp();
        int newXP = oldXP + xpAmount;
        user.setTotalXp(newXP);
        userRepository.save(user);
    }
}
