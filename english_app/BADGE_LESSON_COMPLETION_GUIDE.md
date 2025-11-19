# Hướng Dẫn Xử Lý Huy Hiệu Khi Hoàn Thành Bài Học

## 📋 Tổng Quan

Hệ thống đã được tích hợp logic tự động kiểm tra và trao huy hiệu khi người dùng hoàn thành các bài học. Logic này được xử lý thông qua `BadgeCheckService`.

## 🎯 Các Điểm Tích Hợp

### 1. ✅ Grammar Lesson (Bài Học Ngữ Pháp)

**File**: `GrammarLessonService.java`

**Phương thức**: `completeLesson(Long userId, Long topicId, Long lessonId, String type)`

**Logic xử lý**:
```java
// Sau khi cộng XP cho user
if (shouldAwardXp) {
    xpAwarded = lesson.getXpReward() != null ? lesson.getXpReward() : 100;
    Integer currentXp = user.getTotalXp() != null ? user.getTotalXp() : 0;
    user.setTotalXp(currentXp + xpAwarded);
    userRepository.save(user);
    
    // Kiểm tra và cập nhật huy hiệu
    try {
        badgeCheckService.checkAndUpdateBadges(userId, "grammar");
        log.info("Badge check completed for user {} after completing grammar lesson", userId);
    } catch (Exception e) {
        log.error("Error checking badges for user {}: {}", userId, e.getMessage(), e);
        // Không throw exception để không ảnh hưởng đến flow chính
    }
}
```

**Khi nào được trigger**:
- Khi user hoàn thành lý thuyết (theory) hoặc bài tập (exercise) của một bài học ngữ pháp
- Chỉ khi đó là lần hoàn thành đầu tiên (shouldAwardXp = true)

**Huy hiệu liên quan**:
- Grammar Beginner: Hoàn thành 5 bài ngữ pháp
- Grammar Learner: Hoàn thành 15 bài ngữ pháp
- Grammar Master: Hoàn thành 30 bài ngữ pháp
- Grammar Expert: Hoàn thành 50 bài ngữ pháp

---

### 2. ✅ Vocabulary Flashcard (Thẻ Từ Vựng)

**File**: `VocabWordService.java`

**Phương thức**: `completeWord(CompleteWordRequest request, Long userId)`

**Logic xử lý**:
```java
// Nếu chưa hoàn thành, đánh dấu hoàn thành và cộng XP
if (!progress.getIsCompleted()) {
    progress.setIsCompleted(true);
    progress.setCompletedAt(LocalDateTime.now());
    vocabUserProgressRepository.save(progress);
    
    // Cộng XP cho user
    user.setTotalXp(user.getTotalXp() + word.getXpReward());
    userRepository.save(user);
    
    // Kiểm tra huy hiệu
    badgeCheckService.checkAndUpdateBadges(userId, "VOCABULARY");
}
```

**Khi nào được trigger**:
- Khi user học xong một từ vựng qua flashcard
- Chỉ khi lần đầu tiên hoàn thành từ đó

**Huy hiệu liên quan**:
- Word Collector: Học 20 từ vựng
- Vocabulary Builder: Học 50 từ vựng
- Word Master: Học 100 từ vựng
- Vocabulary Expert: Học 200 từ vựng
- Word Guru: Học 500 từ vựng

---

### 3. ✅ Vocabulary Exercise (Bài Tập Từ Vựng)

**File**: `VocabExerciseService.java`

**Phương thức**: `submitAnswer(Integer questionId, SubmitAnswerRequest request)`

**Logic xử lý**:
```java
if (isCorrect) {
    if (!alreadyCompleted) {
        xpEarned = question.getXpReward() != null ? question.getXpReward() : 0;
        repository.updateUserXP(request.getUserId(), xpEarned);
        totalXp += xpEarned;
        
        repository.updateUserProgress(...);
        
        // Kiểm tra và cập nhật huy hiệu
        try {
            badgeCheckService.checkAndUpdateBadges(request.getUserId().longValue(), "vocabulary");
            System.out.println("Badge check completed for user " + request.getUserId());
        } catch (Exception e) {
            System.err.println("Error checking badges: " + e.getMessage());
        }
    }
}
```

**Khi nào được trigger**:
- Khi user trả lời đúng một câu hỏi bài tập từ vựng
- Chỉ khi câu hỏi đó chưa được hoàn thành trước đó

**Huy hiệu liên quan**:
- Cùng các huy hiệu vocabulary như trên (được tính chung)

---

### 4. ✅ Streak (Chuỗi Ngày Học)

**File**: `UserStreakService.java`

**Phương thức**: `updateStreak(UserEntity user)`

**Logic xử lý**:
```java
// Sau khi cập nhật streak
streak.setLastActivityDate(today);
streak.setUpdatedAt(LocalDateTime.now());
UserStreakEntity savedStreak = userStreakRepository.save(streak);

// Kiểm tra và award streak badges
badgeCheckService.checkAndUpdateBadges(user.getId(), "STREAK");

return savedStreak;
```

**Khi nào được trigger**:
- Khi user login hoặc có hoạt động học tập
- Tự động cập nhật streak dựa trên ngày hoạt động cuối cùng

**Huy hiệu liên quan**:
- Streak Starter: 3 ngày liên tiếp
- Consistent Learner: 7 ngày liên tiếp
- Dedicated Student: 14 ngày liên tiếp
- Study Champion: 30 ngày liên tiếp
- Unstoppable: 50 ngày liên tiếp
- Legendary Learner: 100 ngày liên tiếp

---

## 🔧 Cách Hoạt Động của BadgeCheckService

### Phương thức chính: `checkAndUpdateBadges(Long userId, String conditionType)`

**Các bước xử lý**:

1. **Lấy danh sách badges**: Lấy tất cả badges active theo loại điều kiện
   ```java
   List<BadgeEntity> badges = badgeRepository.findByConditionTypeAndIsActiveTrue(type);
   ```

2. **Tính giá trị hiện tại**: Tính số lượng thành tích hiện tại của user
   ```java
   int currentValue = calculateUserValue(userId, conditionType);
   ```
   
   - **vocabulary**: Đếm số từ vựng đã hoàn thành
   - **grammar**: Đếm số bài ngữ pháp đã hoàn thành
   - **streak**: Lấy số ngày streak hiện tại
   - **writing**: Đếm số bài viết đã hoàn thành
   - **forum**: Đếm số bài viết diễn đàn

3. **Kiểm tra từng badge**: 
   ```java
   for (BadgeEntity badge : badges) {
       processBadge(userId, badge, currentValue);
   }
   ```

4. **Xử lý badge**:
   - Kiểm tra user đã có badge chưa
   - Cập nhật progress (tiến độ)
   - Nếu đạt điều kiện → trao badge

5. **Trao badge** (nếu đạt điều kiện):
   - Tạo record trong `user_badges`
   - Cộng XP thưởng từ badge
   - Cập nhật progress lên 100%
   - Log event

---

## 📊 Badge Progress Tracking

Hệ thống tự động theo dõi tiến độ của user đối với mỗi badge thông qua bảng `user_badge_progress`:

**Các trường quan trọng**:
- `user_id`: ID người dùng
- `badge_id`: ID huy hiệu
- `current_value`: Giá trị hiện tại (VD: 15 bài ngữ pháp)
- `target_value`: Giá trị cần đạt (VD: 30 bài ngữ pháp)
- `progress_percentage`: Phần trăm hoàn thành (VD: 50%)

**Cách tính progress**:
```java
BigDecimal percentage = BigDecimal.valueOf(currentValue)
    .divide(BigDecimal.valueOf(targetValue), 4, RoundingMode.HALF_UP)
    .multiply(BigDecimal.valueOf(100))
    .setScale(2, RoundingMode.HALF_UP);
```

---

## 🎁 Quy Trình Trao Badge

Khi user đạt điều kiện, hệ thống sẽ:

1. ✅ **Tạo record**: Lưu vào bảng `user_badges`
2. 💰 **Cộng XP**: Thêm XP thưởng vào `total_xp` của user
3. 📈 **Cập nhật progress**: Đặt progress = 100%
4. 📝 **Ghi log**: Log thông tin trao badge
5. 🔔 **Notification** (TODO): Gửi thông báo cho user

```java
private void awardBadge(Long userId, BadgeEntity badge) {
    // 1. Tạo record trong user_badges
    UserBadgeEntity userBadge = new UserBadgeEntity();
    userBadge.setUser(user);
    userBadge.setBadge(badge);
    userBadge.setEarnedAt(LocalDateTime.now());
    userBadgeRepository.save(userBadge);

    // 2. Cộng XP cho user
    userService.addXP(userId, badge.getXpReward());

    // 3. Update progress to 100%
    updateBadgeProgress(userId, badge, badge.getConditionValue());

    // 4. Log event
    log.info("🎉 User {} earned badge: '{}' (+{} XP)", userId, badge.getName(), badge.getXpReward());
}
```

---

## 🚨 Error Handling

**Nguyên tắc quan trọng**: Logic badge không được làm gián đoạn flow chính

```java
try {
    badgeCheckService.checkAndUpdateBadges(userId, "grammar");
    log.info("Badge check completed");
} catch (Exception e) {
    log.error("Error checking badges: {}", e.getMessage(), e);
    // KHÔNG throw exception - để user vẫn hoàn thành bài học thành công
}
```

**Lý do**:
- Badge là tính năng phụ, không ảnh hưởng đến học tập
- Nếu có lỗi badge, user vẫn nhận được XP và hoàn thành bài
- Log lỗi để admin có thể debug sau

---

## 📱 API Endpoints Liên Quan

### Lấy tiến độ tất cả badges
```
GET /api/badges/progress?userId={userId}
```

**Response**:
```json
[
  {
    "badgeId": 1,
    "badgeName": "Grammar Beginner",
    "badgeDescription": "Hoàn thành 5 bài học ngữ pháp",
    "iconUrl": "badge_grammar_1.png",
    "conditionType": "GRAMMAR",
    "xpReward": 100,
    "isEarned": false,
    "currentValue": 3,
    "targetValue": 5,
    "progressPercentage": 60.0,
    "earnedAt": null
  }
]
```

### Lấy tiến độ badges theo loại
```
GET /api/badges/progress/type/{type}?userId={userId}
```

Types: `vocabulary`, `grammar`, `writing`, `streak`, `forum`

---

## 🎯 Checklist Khi Thêm Feature Mới

Nếu bạn thêm feature mới cần tích hợp badges:

- [ ] Xác định loại condition (vocabulary, grammar, writing, streak, forum)
- [ ] Inject `BadgeCheckService` vào service
- [ ] Gọi `badgeCheckService.checkAndUpdateBadges(userId, "type")` sau khi user hoàn thành
- [ ] Bọc trong try-catch để không ảnh hưởng flow chính
- [ ] Log thông tin để dễ debug
- [ ] Test với nhiều trường hợp (first time, already completed, error cases)

---

## 🔍 Testing

### Test Case 1: Hoàn thành bài học ngữ pháp lần đầu
1. User chưa có badge Grammar Beginner (cần 5 bài)
2. User hoàn thành bài ngữ pháp thứ 5
3. ✅ Nhận XP từ bài học
4. ✅ Nhận badge "Grammar Beginner"
5. ✅ Nhận XP thưởng từ badge (100 XP)

### Test Case 2: Hoàn thành bài đã làm trước đó
1. User đã hoàn thành bài X
2. User làm lại bài X
3. ✅ Không nhận thêm XP
4. ❌ Không trigger badge check (do shouldAwardXp = false)

### Test Case 3: Đạt nhiều milestone cùng lúc
1. User có 4 bài ngữ pháp
2. Database có badges: 5 bài, 15 bài, 30 bài
3. User hoàn thành bài thứ 5
4. ✅ Chỉ nhận badge 5 bài (chưa đạt 15 bài)
5. Progress của badge 15 bài được cập nhật: 5/15 = 33.33%

---

## 💡 Tips & Best Practices

1. **Không gọi badge check nếu không có thay đổi**: 
   - Chỉ gọi khi user thực sự hoàn thành mới
   - Tránh gọi khi user làm lại bài cũ

2. **Luôn wrap trong try-catch**:
   - Badge là feature phụ, không được làm crash app
   - Log đầy đủ để debug

3. **Transaction handling**:
   - `BadgeCheckService.checkAndUpdateBadges()` đã có `@Transactional`
   - Nếu service gọi đã có transaction, badge sẽ dùng chung transaction

4. **Performance**:
   - Badge check query database nhiều lần
   - Cân nhắc cache nếu số lượng user lớn

5. **Logging**:
   - Sử dụng SLF4J logger
   - Level: INFO cho success, ERROR cho exception
   - Include userId và badge info để dễ trace

---

## 🐛 Troubleshooting

### Vấn đề: Badge không được trao dù đã đạt điều kiện

**Kiểm tra**:
1. Badge có active không? (`is_active = true`)
2. ConditionType có đúng không? (VOCABULARY, GRAMMAR, STREAK, ...)
3. `calculateUserValue()` có tính đúng không?
4. User đã có badge đó chưa?

**Debug**:
```java
log.debug("User {} current value for {}: {}", userId, conditionType, currentValue);
log.debug("Badge {} requires: {}", badge.getId(), badge.getConditionValue());
```

### Vấn đề: Lỗi NullPointerException

**Nguyên nhân thường gặp**:
- User không tồn tại
- Badge không tồn tại
- Repository trả về null

**Giải pháp**:
- Luôn check null trước khi access
- Dùng Optional.orElse() hoặc orElseThrow()

---

## 📚 Related Documentation

- [BADGE_LOGIC_GUIDE.md](./BADGE_LOGIC_GUIDE.md) - Logic tổng quan về badge system
- [README_BADGES.md](./README_BADGES.md) - Danh sách các badges trong hệ thống
- [BADGE_CONNECTION_VALIDATION.md](./BADGE_CONNECTION_VALIDATION.md) - Kiểm tra kết nối database

---

## ✅ Kết Luận

Hệ thống xử lý huy hiệu khi hoàn thành bài học đã được tích hợp đầy đủ cho:

- ✅ Grammar Lessons (Ngữ pháp)
- ✅ Vocabulary Flashcards (Từ vựng - flashcard)
- ✅ Vocabulary Exercises (Từ vựng - bài tập)
- ✅ Streak (Chuỗi ngày học)

Hệ thống hoạt động tự động, không yêu cầu can thiệp thủ công, và được thiết kế để không ảnh hưởng đến flow học tập chính của user.

