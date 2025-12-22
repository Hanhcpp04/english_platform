package com.back_end.english_app.service.user;

import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.UserStreakEntity;
import com.back_end.english_app.repository.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserStreakService {

    private final UserStreakRepository userStreakRepository;
    private final BadgeCheckService badgeCheckService;


    @Transactional
    public UserStreakEntity getOrCreateStreak(UserEntity user) {
        return userStreakRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserStreakEntity newStreak = new UserStreakEntity();
                    newStreak.setUser(user);
                    newStreak.setCurrentStreak(0);
                    newStreak.setLongestStreak(0);
                    newStreak.setTotalStudyDays(0);
                    newStreak.setCreatedAt(LocalDateTime.now());
                    newStreak.setUpdatedAt(LocalDateTime.now());
                    return userStreakRepository.save(newStreak);
                });
    }

    /**
     * Cập nhật streak khi user login hoặc hoạt động
     */
    @Transactional
    public UserStreakEntity updateStreak(UserEntity user) {
        UserStreakEntity streak = getOrCreateStreak(user);
        LocalDate today = LocalDate.now();
        LocalDate lastActivity = streak.getLastActivityDate();

        if (lastActivity == null) {
            // Lần đầu tiên hoạt động
            streak.setCurrentStreak(1);
            streak.setLongestStreak(1);
            streak.setStreakStartDate(today);
            streak.setLongestStreakDate(today);
            streak.setTotalStudyDays(1);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastActivity, today);

            if (daysBetween == 0) {
                // Hoạt động trong cùng ngày, không thay đổi streak
                // Chỉ update updatedAt
            } else if (daysBetween == 1) {
                // Hoạt động liên tiếp ngày tiếp theo
                streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                streak.setTotalStudyDays(streak.getTotalStudyDays() + 1);

                // Cập nhật longest streak nếu current streak vượt qua
                if (streak.getCurrentStreak() > streak.getLongestStreak()) {
                    streak.setLongestStreak(streak.getCurrentStreak());
                    streak.setLongestStreakDate(today);
                }
            } else {
                // Đã bỏ lỡ ít nhất 1 ngày, reset streak
                streak.setCurrentStreak(1);
                streak.setStreakStartDate(today);
                streak.setTotalStudyDays(streak.getTotalStudyDays() + 1);
            }
        }

        // Cập nhật last activity date và updated time
        streak.setLastActivityDate(today);
        streak.setUpdatedAt(LocalDateTime.now());

        UserStreakEntity savedStreak = userStreakRepository.save(streak);

        // Kiểm tra và award streak badges
        badgeCheckService.checkAndUpdateBadges(user.getId(), "STREAK");

        return savedStreak;
    }

    /**
     * Lấy thông tin streak của user, tạo mới nếu chưa có
     */
    @Transactional(readOnly = true)
    public UserStreakEntity getUserStreak(Long userId) {
        return userStreakRepository.findByUserId(userId)
                .orElse(createDefaultStreak());
    }

    /**
     * Tạo streak mặc định để trả về khi user chưa có streak
     */
    private UserStreakEntity createDefaultStreak() {
        UserStreakEntity defaultStreak = new UserStreakEntity();
        defaultStreak.setCurrentStreak(0);
        defaultStreak.setLongestStreak(0);
        defaultStreak.setTotalStudyDays(0);
        defaultStreak.setLastActivityDate(null);
        defaultStreak.setStreakStartDate(null);
        defaultStreak.setLongestStreakDate(null);
        return defaultStreak;
    }
}

