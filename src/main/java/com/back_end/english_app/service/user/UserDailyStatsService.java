package com.back_end.english_app.service.user;

import com.back_end.english_app.entity.UserDailyStatsEntity;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserDailyStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDailyStatsService {
    private final UserDailyStatsRepository userDailyStatsRepository;

    /**
     * Lấy hoặc tạo mới UserDailyStats cho user và ngày hiện tại
     */
    @Transactional
    public UserDailyStatsEntity getOrCreateTodayStats(UserEntity user) {
        try {
            LocalDate today = LocalDate.now();
            Optional<UserDailyStatsEntity> existingStats = userDailyStatsRepository
                    .findByUserAndDate(user, today);

            if (existingStats.isPresent()) {
                log.debug("Found existing daily stats for user {} on date {}", user.getId(), today);
                return existingStats.get();
            }

            // Tạo mới nếu chưa có
            UserDailyStatsEntity newStats = new UserDailyStatsEntity();
            newStats.setUser(user);
            newStats.setDate(today);
            newStats.setVocabLearned(0);
            newStats.setGrammarCompleted(0);
            newStats.setExercisesDone(0);
            newStats.setWritingSubmitted(0);
            newStats.setForumPosts(0);
            newStats.setForumComments(0);
            newStats.setStudyTimeMinutes(0);
            newStats.setXpEarned(0);
            newStats.setIsStudyDay(false);

            UserDailyStatsEntity saved = userDailyStatsRepository.save(newStats);
            log.info("Created new daily stats for user {} on date {}", user.getId(), today);
            return saved;
        } catch (Exception e) {
            log.error("Error getting or creating daily stats for user {}: {}", user.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to get or create daily stats", e);
        }
    }

    /**
     * Cập nhật khi user học từ vựng
     */
    @Transactional
    public void recordVocabLearned(UserEntity user, int count) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setVocabLearned(stats.getVocabLearned() + count);
            stats.setIsStudyDay(true);
            userDailyStatsRepository.save(stats);
            log.info("Updated vocab learned for user {}: +{} (total: {})", 
                    user.getId(), count, stats.getVocabLearned());
        } catch (Exception e) {
            log.error("Failed to record vocab learned for user {}: {}", user.getId(), e.getMessage(), e);
            // Không throw exception để không làm gián đoạn flow chính
        }
    }

    /**
     * Cập nhật khi user hoàn thành grammar lesson
     */
    @Transactional
    public void recordGrammarCompleted(UserEntity user, int count) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setGrammarCompleted(stats.getGrammarCompleted() + count);
            stats.setIsStudyDay(true);
            userDailyStatsRepository.save(stats);
            log.info("Updated grammar completed for user {}: +{} (total: {})", 
                    user.getId(), count, stats.getGrammarCompleted());
        } catch (Exception e) {
            log.error("Failed to record grammar completed for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Cập nhật khi user làm bài tập
     */
    @Transactional
    public void recordExerciseDone(UserEntity user, int count) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setExercisesDone(stats.getExercisesDone() + count);
            stats.setIsStudyDay(true);
            userDailyStatsRepository.save(stats);
            log.info("Updated exercises done for user {}: +{} (total: {})", 
                    user.getId(), count, stats.getExercisesDone());
        } catch (Exception e) {
            log.error("Failed to record exercise done for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Cập nhật khi user nộp bài writing
     */
    @Transactional
    public void recordWritingSubmitted(UserEntity user, int count) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setWritingSubmitted(stats.getWritingSubmitted() + count);
            stats.setIsStudyDay(true);
            userDailyStatsRepository.save(stats);
            log.info("Updated writing submitted for user {}: +{} (total: {})", 
                    user.getId(), count, stats.getWritingSubmitted());
        } catch (Exception e) {
            log.error("Failed to record writing submitted for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Cập nhật khi user đăng bài forum
     */
    @Transactional
    public void recordForumPost(UserEntity user, int count) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setForumPosts(stats.getForumPosts() + count);
            stats.setIsStudyDay(true);
            userDailyStatsRepository.save(stats);
            log.info("Updated forum posts for user {}: +{} (total: {})", 
                    user.getId(), count, stats.getForumPosts());
        } catch (Exception e) {
            log.error("Failed to record forum post for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Cập nhật khi user comment forum
     */
    @Transactional
    public void recordForumComment(UserEntity user, int count) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setForumComments(stats.getForumComments() + count);
            stats.setIsStudyDay(true);
            userDailyStatsRepository.save(stats);
            log.info("Updated forum comments for user {}: +{} (total: {})", 
                    user.getId(), count, stats.getForumComments());
        } catch (Exception e) {
            log.error("Failed to record forum comment for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Cập nhật XP earned trong ngày
     */
    @Transactional
    public void recordXpEarned(UserEntity user, int xp) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setXpEarned(stats.getXpEarned() + xp);
            userDailyStatsRepository.save(stats);
            log.info("Updated XP earned for user {}: +{} (total: {})", 
                    user.getId(), xp, stats.getXpEarned());
        } catch (Exception e) {
            log.error("Failed to record XP earned for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Cập nhật thời gian học (phút)
     */
    @Transactional
    public void recordStudyTime(UserEntity user, int minutes) {
        try {
            UserDailyStatsEntity stats = getOrCreateTodayStats(user);
            stats.setStudyTimeMinutes(stats.getStudyTimeMinutes() + minutes);
            stats.setIsStudyDay(true);
            userDailyStatsRepository.save(stats);
            log.info("Updated study time for user {}: +{} minutes (total: {})", 
                    user.getId(), minutes, stats.getStudyTimeMinutes());
        } catch (Exception e) {
            log.error("Failed to record study time for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }
}
