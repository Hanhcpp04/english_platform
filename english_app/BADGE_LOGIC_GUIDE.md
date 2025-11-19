# Hướng Dẫn Xử Lý Logic Huy Hiệu (Badge System)

## 📋 Tổng Quan

Hệ thống huy hiệu được thiết kế để tự động trao thưởng cho user khi họ đạt được các mốc thành tích nhất định.

## 🗄️ Cấu Trúc Database

### 1. Bảng `badges`
- Lưu trữ thông tin các huy hiệu
- `condition_type`: Loại điều kiện (vocabulary, grammar, writing, streak, etc.)
- `condition_value`: Giá trị cần đạt để nhận huy hiệu
- `xp_reward`: Số XP thưởng khi đạt huy hiệu

### 2. Bảng `user_badges`
- Lưu các huy hiệu mà user đã đạt được
- `earned_at`: Thời điểm nhận huy hiệu

### 3. Bảng `user_badge_progress` (MỚI)
- Theo dõi tiến độ của user với từng huy hiệu
- `current_value`: Giá trị hiện tại
- `target_value`: Giá trị mục tiêu (lấy từ badges.condition_value)
- `progress_percentage`: Phần trăm hoàn thành

## 🎯 Các Loại Huy Hiệu

### 1. **vocabulary** - Học từ vựng
```sql
-- Ví dụ: "Word Master" - Học 100 từ vựng
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('Word Master', 'Hoàn thành 100 từ vựng', 'vocabulary', 100, 500, '/icons/word-master.png');

-- "Vocabulary Expert" - Học 500 từ vựng
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('Vocabulary Expert', 'Hoàn thành 500 từ vựng', 'vocabulary', 500, 2000, '/icons/vocab-expert.png');
```

**Cách tính:**
- Đếm số từ vựng đã hoàn thành trong `vocab_user_progress` với `is_completed = TRUE`

### 2. **grammar** - Học ngữ pháp
```sql
-- Ví dụ: "Grammar Guru" - Hoàn thành 50 bài học ngữ pháp
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('Grammar Guru', 'Hoàn thành 50 bài ngữ pháp', 'grammar', 50, 800, '/icons/grammar-guru.png');
```

**Cách tính:**
- Đếm số bài học ngữ pháp đã hoàn thành trong `user_grammar_progress` với `is_completed = TRUE`

### 3. **writing** - Viết luận
```sql
-- Ví dụ: "Writing Master" - Viết 20 bài luận
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('Writing Master', 'Hoàn thành 20 bài viết', 'writing', 20, 1000, '/icons/writing-master.png');

-- "Perfect Writer" - Đạt điểm 90+ cho 10 bài viết
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('Perfect Writer', 'Đạt điểm 90+ cho 10 bài viết', 'writing', 10, 1500, '/icons/perfect-writer.png');
```

**Cách tính:**
- Đếm số bài viết đã hoàn thành trong `writing_prompts` với `is_completed = TRUE`
- Hoặc đếm bài có `overall_score >= 90`

### 4. **forum** - Tham gia diễn đàn
```sql
-- Ví dụ: "Forum Star" - Tạo 50 bài viết trên diễn đàn
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('Forum Star', 'Tạo 50 bài viết diễn đàn', 'forum', 50, 600, '/icons/forum-star.png');
```

**Cách tính:**
- Đếm số bài viết trong `forum_posts` của user

### 5. **streak** - Chuỗi ngày học liên tục
```sql
-- Ví dụ: "7 Day Streak" - Học liên tục 7 ngày
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('7 Day Streak', 'Học liên tục 7 ngày', 'streak', 7, 300, '/icons/streak-7.png');

-- "30 Day Streak" - Học liên tục 30 ngày
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('30 Day Streak', 'Học liên tục 30 ngày', 'streak', 30, 1500, '/icons/streak-30.png');
```

**Cách tính:**
- Lấy `current_streak` từ bảng `user_streaks`

### 6. **accuracy** - Độ chính xác
```sql
-- Ví dụ: "Perfect Score" - Đạt 100% accuracy trong 10 bài tập
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) 
VALUES ('Perfect Score', 'Đạt 100% chính xác 10 lần', 'accuracy', 10, 800, '/icons/perfect-score.png');
```

**Cách tính:**
- Đếm số lần `accuracy_rate = 100` trong `user_daily_stats`

## 🔄 Quy Trình Kiểm Tra & Trao Huy Hiệu

### **Phương án 1: Kiểm tra theo sự kiện (Event-Driven) - ĐỀ XUẤT**

Mỗi khi user hoàn thành một hoạt động, hệ thống sẽ kiểm tra và cập nhật tiến độ huy hiệu:

#### **Các trigger points (điểm kích hoạt kiểm tra):**

1. **Sau khi user hoàn thành từ vựng:**
   ```java
   // Trong VocabProgressService.java
   public void completeWord(Long userId, Long wordId) {
       // 1. Cập nhật vocab_user_progress
       updateVocabProgress(userId, wordId);
       
       // 2. Kiểm tra và cập nhật badges
       badgeService.checkAndUpdateBadges(userId, "vocabulary");
   }
   ```

2. **Sau khi user hoàn thành bài ngữ pháp:**
   ```java
   // Trong GrammarProgressService.java
   public void completeLesson(Long userId, Long lessonId) {
       updateGrammarProgress(userId, lessonId);
       badgeService.checkAndUpdateBadges(userId, "grammar");
   }
   ```

3. **Sau khi user submit bài viết:**
   ```java
   // Trong WritingService.java
   public void submitWriting(Long userId, Long promptId) {
       submitPrompt(userId, promptId);
       badgeService.checkAndUpdateBadges(userId, "writing");
   }
   ```

4. **Sau khi cập nhật streak:**
   ```java
   // Trong StreakService.java
   public void updateStreak(Long userId) {
       calculateStreak(userId);
       badgeService.checkAndUpdateBadges(userId, "streak");
   }
   ```

5. **Sau khi tạo bài viết diễn đàn:**
   ```java
   // Trong ForumService.java
   public void createPost(Long userId, ForumPostDTO postDTO) {
       savePost(userId, postDTO);
       badgeService.checkAndUpdateBadges(userId, "forum");
   }
   ```

### **Code Implementation cho BadgeService:**

```java
@Service
public class BadgeService {
    
    @Autowired
    private BadgeRepository badgeRepository;
    
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    
    @Autowired
    private UserBadgeProgressRepository userBadgeProgressRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * Kiểm tra và cập nhật badges cho user theo loại hoạt động
     */
    @Transactional
    public void checkAndUpdateBadges(Long userId, String conditionType) {
        // 1. Lấy tất cả badges active theo loại
        List<Badge> badges = badgeRepository.findByConditionTypeAndIsActive(conditionType, true);
        
        // 2. Tính giá trị hiện tại của user
        int currentValue = calculateUserValue(userId, conditionType);
        
        // 3. Kiểm tra từng badge
        for (Badge badge : badges) {
            // 3.1. Kiểm tra xem user đã có badge này chưa
            boolean alreadyEarned = userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId());
            
            if (!alreadyEarned) {
                // 3.2. Cập nhật hoặc tạo progress
                updateBadgeProgress(userId, badge, currentValue);
                
                // 3.3. Nếu đạt điều kiện, trao badge
                if (currentValue >= badge.getConditionValue()) {
                    awardBadge(userId, badge);
                }
            }
        }
    }
    
    /**
     * Tính giá trị hiện tại của user theo loại điều kiện
     */
    private int calculateUserValue(Long userId, String conditionType) {
        switch (conditionType) {
            case "vocabulary":
                // Đếm số từ vựng đã hoàn thành
                return vocabProgressRepository.countByUserIdAndIsCompleted(userId, true);
                
            case "grammar":
                // Đếm số bài ngữ pháp đã hoàn thành
                return grammarProgressRepository.countByUserIdAndIsCompleted(userId, true);
                
            case "writing":
                // Đếm số bài viết đã hoàn thành
                return writingPromptRepository.countByUserIdAndIsCompleted(userId, true);
                
            case "forum":
                // Đếm số bài viết diễn đàn
                return forumPostRepository.countByUserIdAndIsActive(userId, true);
                
            case "streak":
                // Lấy streak hiện tại
                UserStreak streak = userStreakRepository.findByUserId(userId);
                return streak != null ? streak.getCurrentStreak() : 0;
                
            case "accuracy":
                // Đếm số lần đạt 100% accuracy
                return dailyStatsRepository.countByUserIdAndAccuracyRate(userId, 100.0);
                
            default:
                return 0;
        }
    }
    
    /**
     * Cập nhật tiến độ badge
     */
    private void updateBadgeProgress(Long userId, Badge badge, int currentValue) {
        UserBadgeProgress progress = userBadgeProgressRepository
            .findByUserIdAndBadgeId(userId, badge.getId())
            .orElse(new UserBadgeProgress());
            
        progress.setUserId(userId);
        progress.setBadgeId(badge.getId());
        progress.setCurrentValue(currentValue);
        progress.setTargetValue(badge.getConditionValue());
        
        // Tính phần trăm
        double percentage = (double) currentValue / badge.getConditionValue() * 100;
        progress.setProgressPercentage(Math.min(percentage, 100.0));
        
        userBadgeProgressRepository.save(progress);
    }
    
    /**
     * Trao badge cho user
     */
    @Transactional
    private void awardBadge(Long userId, Badge badge) {
        // 1. Tạo record trong user_badges
        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadgeId(badge.getId());
        userBadge.setEarnedAt(Timestamp.valueOf(LocalDateTime.now()));
        userBadgeRepository.save(userBadge);
        
        // 2. Cộng XP cho user
        userService.addXP(userId, badge.getXpReward());
        
        // 3. Gửi notification cho user (optional)
        notificationService.sendBadgeNotification(userId, badge);
        
        // 4. Log event
        log.info("User {} earned badge: {} ({})", userId, badge.getName(), badge.getXpReward() + " XP");
    }
    
    /**
     * Lấy tất cả badges của user
     */
    public List<UserBadgeDTO> getUserBadges(Long userId) {
        return userBadgeRepository.findAllByUserIdWithDetails(userId);
    }
    
    /**
     * Lấy tiến độ của tất cả badges
     */
    public List<BadgeProgressDTO> getBadgeProgress(Long userId) {
        List<Badge> allBadges = badgeRepository.findByIsActive(true);
        List<BadgeProgressDTO> progressList = new ArrayList<>();
        
        for (Badge badge : allBadges) {
            BadgeProgressDTO dto = new BadgeProgressDTO();
            dto.setBadge(badge);
            
            // Kiểm tra đã earned chưa
            boolean earned = userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId());
            dto.setEarned(earned);
            
            if (!earned) {
                // Lấy progress
                UserBadgeProgress progress = userBadgeProgressRepository
                    .findByUserIdAndBadgeId(userId, badge.getId())
                    .orElse(null);
                    
                if (progress != null) {
                    dto.setCurrentValue(progress.getCurrentValue());
                    dto.setTargetValue(progress.getTargetValue());
                    dto.setProgressPercentage(progress.getProgressPercentage());
                } else {
                    dto.setCurrentValue(0);
                    dto.setTargetValue(badge.getConditionValue());
                    dto.setProgressPercentage(0.0);
                }
            }
            
            progressList.add(dto);
        }
        
        return progressList;
    }
}
```

### **Phương án 2: Kiểm tra định kỳ (Scheduled) - BACKUP**

Nếu không muốn kiểm tra realtime, có thể dùng scheduled job:

```java
@Component
public class BadgeCheckScheduler {
    
    @Autowired
    private BadgeService badgeService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Chạy mỗi 1 giờ để kiểm tra badges cho tất cả users active
     */
    @Scheduled(cron = "0 0 * * * *") // Mỗi giờ
    public void checkAllUserBadges() {
        List<User> activeUsers = userRepository.findByIsActive(true);
        
        for (User user : activeUsers) {
            try {
                // Kiểm tra tất cả loại badges
                badgeService.checkAndUpdateBadges(user.getId(), "vocabulary");
                badgeService.checkAndUpdateBadges(user.getId(), "grammar");
                badgeService.checkAndUpdateBadges(user.getId(), "writing");
                badgeService.checkAndUpdateBadges(user.getId(), "forum");
                badgeService.checkAndUpdateBadges(user.getId(), "streak");
                badgeService.checkAndUpdateBadges(user.getId(), "accuracy");
            } catch (Exception e) {
                log.error("Error checking badges for user {}: {}", user.getId(), e.getMessage());
            }
        }
    }
}
```

## 📊 Các Query SQL Hữu Ích

### 1. Lấy tất cả badges của user
```sql
SELECT b.*, ub.earned_at
FROM badges b
INNER JOIN user_badges ub ON b.id = ub.badge_id
WHERE ub.user_id = ?
ORDER BY ub.earned_at DESC;
```

### 2. Lấy tiến độ badges chưa đạt được
```sql
SELECT 
    b.*,
    COALESCE(ubp.current_value, 0) as current_value,
    b.condition_value as target_value,
    COALESCE(ubp.progress_percentage, 0) as progress_percentage
FROM badges b
LEFT JOIN user_badge_progress ubp ON b.id = ubp.badge_id AND ubp.user_id = ?
WHERE b.is_active = TRUE
AND NOT EXISTS (
    SELECT 1 FROM user_badges ub 
    WHERE ub.badge_id = b.id AND ub.user_id = ?
)
ORDER BY ubp.progress_percentage DESC;
```

### 3. Đếm số từ vựng đã học
```sql
SELECT COUNT(*) 
FROM vocab_user_progress 
WHERE user_id = ? AND is_completed = TRUE;
```

### 4. Đếm số bài ngữ pháp đã hoàn thành
```sql
SELECT COUNT(DISTINCT lesson_id) 
FROM user_grammar_progress 
WHERE user_id = ? AND is_completed = TRUE;
```

### 5. Đếm số bài viết đã hoàn thành
```sql
SELECT COUNT(*) 
FROM writing_prompts 
WHERE user_id = ? AND is_completed = TRUE;
```

### 6. Lấy streak hiện tại
```sql
SELECT current_streak, longest_streak 
FROM user_streaks 
WHERE user_id = ?;
```

## 🎨 Best Practices

### 1. **Tối ưu hiệu suất:**
   - Cache danh sách badges trong Redis
   - Chỉ kiểm tra badges liên quan đến hoạt động vừa làm
   - Sử dụng index đúng cách

### 2. **Notification:**
   - Gửi notification realtime khi đạt badge
   - Hiển thị animation celebration
   - Email/push notification (optional)

### 3. **Logging:**
   - Log mọi lần trao badge
   - Track badge progress changes
   - Monitor badge achievement rates

### 4. **Testing:**
   - Unit test cho logic tính toán
   - Integration test cho flow hoàn chỉnh
   - Test edge cases (đạt nhiều badges cùng lúc)

## 🚀 Ví Dụ Dữ Liệu Mẫu

```sql
-- Insert sample badges
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url) VALUES
('First Word', 'Học từ vựng đầu tiên', 'vocabulary', 1, 10, '/icons/first-word.png'),
('Word Learner', 'Học 50 từ vựng', 'vocabulary', 50, 200, '/icons/word-learner.png'),
('Word Master', 'Học 100 từ vựng', 'vocabulary', 100, 500, '/icons/word-master.png'),
('Vocabulary Expert', 'Học 500 từ vựng', 'vocabulary', 500, 2000, '/icons/vocab-expert.png'),

('Grammar Newbie', 'Hoàn thành bài ngữ pháp đầu tiên', 'grammar', 1, 10, '/icons/grammar-newbie.png'),
('Grammar Student', 'Hoàn thành 20 bài ngữ pháp', 'grammar', 20, 300, '/icons/grammar-student.png'),
('Grammar Guru', 'Hoàn thành 50 bài ngữ pháp', 'grammar', 50, 800, '/icons/grammar-guru.png'),

('Writer', 'Viết bài luận đầu tiên', 'writing', 1, 20, '/icons/writer.png'),
('Writing Master', 'Hoàn thành 20 bài viết', 'writing', 20, 1000, '/icons/writing-master.png'),

('Forum Newbie', 'Tạo bài viết đầu tiên', 'forum', 1, 10, '/icons/forum-newbie.png'),
('Forum Star', 'Tạo 50 bài viết diễn đàn', 'forum', 50, 600, '/icons/forum-star.png'),

('3 Day Streak', 'Học liên tục 3 ngày', 'streak', 3, 50, '/icons/streak-3.png'),
('7 Day Streak', 'Học liên tục 7 ngày', 'streak', 7, 300, '/icons/streak-7.png'),
('30 Day Streak', 'Học liên tục 30 ngày', 'streak', 30, 1500, '/icons/streak-30.png'),
('100 Day Streak', 'Học liên tục 100 ngày', 'streak', 100, 5000, '/icons/streak-100.png'),

('Perfect Score', 'Đạt 100% chính xác 10 lần', 'accuracy', 10, 800, '/icons/perfect-score.png');
```

## 📝 Tóm Tắt

**Khuyến nghị:** Sử dụng **Phương án 1 (Event-Driven)** cho trải nghiệm user tốt nhất:

1. ✅ Trao badge ngay lập tức khi đạt điều kiện
2. ✅ Hiệu suất tốt hơn (chỉ check khi cần)
3. ✅ Realtime notification
4. ✅ User experience tốt hơn

**Implementation steps:**

1. Thêm bảng `user_badge_progress` vào database
2. Tạo Entity, Repository, Service cho Badge system
3. Tích hợp `badgeService.checkAndUpdateBadges()` vào các service khác
4. Implement API endpoints để hiển thị badges & progress
5. Test kỹ lưỡng toàn bộ flow

---

Chúc bạn triển khai thành công! 🎉

