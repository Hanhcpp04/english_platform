package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.respones.admin.AdminDashboardDTO;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {
    private final UserRepository userRepository;
    private final VocabWordRepository vocabWordRepository;
    private final GrammarLessonRepository grammarLessonRepository;
    private final VocabExerciseQuestionRepository vocabExerciseQuestionRepository;
    private final GrammarQuestionRepository grammarQuestionRepository;
    private final WritingPromptRepository writingPromptRepository;
    private final ForumPostRepository forumPostRepository;
    private final VocabTopicRepository vocabTopicRepository;
    private final UserVocabProgressRepository userVocabProgressRepository;
    private final UserGrammarProgressRepository userGrammarProgressRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;

    public AdminDashboardDTO getAdminDashboard() {
        log.info("Fetching admin dashboard data");

        return AdminDashboardDTO.builder()
                .stats(getStatCards())
                .userGrowthData(getUserGrowthData())
                .activityDistribution(getActivityDistribution())
                .topicPerformance(getTopicPerformance())
                .recentActivities(getRecentActivities())
                .build();
    }

    private List<AdminDashboardDTO.StatCardDTO> getStatCards() {
        List<AdminDashboardDTO.StatCardDTO> stats = new ArrayList<>();

        // Tổng người dùng với % thay đổi so với tháng trước
        long totalUsers = userRepository.count();
        LocalDateTime lastMonthStart = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long usersLastMonth = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isBefore(lastMonthStart))
                .count();
        String userChange = calculatePercentageChange(usersLastMonth, totalUsers);
        
        stats.add(AdminDashboardDTO.StatCardDTO.builder()
                .title("Tổng người dùng")
                .value(String.valueOf(totalUsers))
                .change(userChange)
                .isIncrease(totalUsers >= usersLastMonth)
                .icon("Users")
                .color("blue")
                .build());

        // Số từ vựng
        long totalVocab = vocabWordRepository.count();
        stats.add(AdminDashboardDTO.StatCardDTO.builder()
                .title("Số từ vựng")
                .value(String.valueOf(totalVocab))
                .change("+8.2%")
                .isIncrease(true)
                .icon("BookOpen")
                .color("green")
                .build());

        // Bài học ngữ pháp
        long totalGrammarLessons = grammarLessonRepository.count();
        stats.add(AdminDashboardDTO.StatCardDTO.builder()
                .title("Bài học ngữ pháp")
                .value(String.valueOf(totalGrammarLessons))
                .change("+5.1%")
                .isIncrease(true)
                .icon("GraduationCap")
                .color("purple")
                .build());

        // Bài tập đang hoạt động
        long totalVocabExercises = vocabExerciseQuestionRepository.countByIsActiveTrue();
        long totalGrammarExercises = grammarQuestionRepository.countByIsActiveTrue();
        long totalExercises = totalVocabExercises + totalGrammarExercises;
        stats.add(AdminDashboardDTO.StatCardDTO.builder()
                .title("Bài tập đang hoạt động")
                .value(String.valueOf(totalExercises))
                .change("+3.2%")
                .isIncrease(true)
                .icon("Target")
                .color("orange")
                .build());

        // Tổng XP
        Long totalXp = userRepository.findAll().stream()
                .mapToLong(UserEntity::getTotalXp)
                .sum();
        String xpValue = formatXp(totalXp);
        stats.add(AdminDashboardDTO.StatCardDTO.builder()
                .title("Tổng XP")
                .value(xpValue)
                .change("+18.3%")
                .isIncrease(true)
                .icon("Award")
                .color("yellow")
                .build());

        // Bài nộp viết
        long totalWritingSubmissions = writingPromptRepository.countByIsCompletedTrue();
        stats.add(AdminDashboardDTO.StatCardDTO.builder()
                .title("Bài nộp viết")
                .value(String.valueOf(totalWritingSubmissions))
                .change("+15.7%")
                .isIncrease(true)
                .icon("FileText")
                .color("indigo")
                .build());

        return stats;
    }

    private String calculatePercentageChange(long oldValue, long newValue) {
        if (oldValue == 0) {
            return newValue > 0 ? "+100%" : "0%";
        }
        double change = ((newValue - oldValue) * 100.0) / oldValue;
        String sign = change >= 0 ? "+" : "";
        return String.format("%s%.1f%%", sign, change);
    }

    private String formatXp(Long xp) {
        if (xp >= 1_000_000) {
            return String.format("%.1fM", xp / 1_000_000.0);
        } else if (xp >= 1_000) {
            return String.format("%.1fK", xp / 1_000.0);
        }
        return String.valueOf(xp);
    }

    private List<AdminDashboardDTO.UserGrowthDataDTO> getUserGrowthData() {
        List<AdminDashboardDTO.UserGrowthDataDTO> growthData = new ArrayList<>();
        
        // Get last 6 months data
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("'Thg 'M");

        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
            
            // Đếm users đã tồn tại đến cuối tháng
            long usersInMonth = userRepository.findAll().stream()
                    .filter(u -> u.getCreatedAt() != null && 
                                !u.getCreatedAt().isAfter(monthEnd))
                    .count();
            
            // Đếm active users: users có is_study_day = true trong tháng đó
            java.time.LocalDate monthStartDate = monthStart.toLocalDate();
            java.time.LocalDate monthEndDate = monthEnd.toLocalDate();
            
            Long activeUsersCount = userDailyStatsRepository.countDistinctActiveUsersBetweenDates(
                monthStartDate, monthEndDate);
            long activeUsers = activeUsersCount != null ? activeUsersCount : 0L;
            
            log.info("Month: {} | Start: {} | End: {} | Total Users: {} | Active Users: {}", 
                    monthStart.format(monthFormatter), monthStartDate, monthEndDate, usersInMonth, activeUsers);

            growthData.add(AdminDashboardDTO.UserGrowthDataDTO.builder()
                    .month(monthStart.format(monthFormatter))
                    .users((int) usersInMonth)
                    .active((int) activeUsers)
                    .build());
        }

        return growthData;
    }

    private List<AdminDashboardDTO.ActivityDistributionDTO> getActivityDistribution() {
        List<AdminDashboardDTO.ActivityDistributionDTO> distribution = new ArrayList<>();

        // Lấy dữ liệu từ user_daily_stats của 30 ngày gần nhất
        java.time.LocalDate endDate = java.time.LocalDate.now();
        java.time.LocalDate startDate = endDate.minusDays(30);
        
        Long vocabLearned = userDailyStatsRepository.sumVocabLearnedBetweenDates(startDate, endDate);
        Long grammarCompleted = userDailyStatsRepository.sumGrammarCompletedBetweenDates(startDate, endDate);
        Long writingSubmitted = userDailyStatsRepository.sumWritingSubmittedBetweenDates(startDate, endDate);
        Long exercisesDone = userDailyStatsRepository.sumExercisesDoneBetweenDates(startDate, endDate);
        
        // Handle null values
        long vocabCount = vocabLearned != null ? vocabLearned : 0L;
        long grammarCount = grammarCompleted != null ? grammarCompleted : 0L;
        long writingCount = writingSubmitted != null ? writingSubmitted : 0L;
        long exerciseCount = exercisesDone != null ? exercisesDone : 0L;
        
        long total = vocabCount + grammarCount + writingCount + exerciseCount;
        
        if (total > 0) {
            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Từ vựng")
                    .value((int) Math.round((vocabCount * 100.0) / total))
                    .color("#3b82f6")
                    .build());

            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Ngữ pháp")
                    .value((int) Math.round((grammarCount * 100.0) / total))
                    .color("#8b5cf6")
                    .build());

            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Viết")
                    .value((int) Math.round((writingCount * 100.0) / total))
                    .color("#10b981")
                    .build());

            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Bài tập")
                    .value((int) Math.round((exerciseCount * 100.0) / total))
                    .color("#f59e0b")
                    .build());
        } else {
            // Dữ liệu mặc định nếu chưa có hoạt động
            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Từ vựng")
                    .value(35)
                    .color("#3b82f6")
                    .build());
            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Ngữ pháp")
                    .value(28)
                    .color("#8b5cf6")
                    .build());
            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Viết")
                    .value(22)
                    .color("#10b981")
                    .build());
            distribution.add(AdminDashboardDTO.ActivityDistributionDTO.builder()
                    .name("Bài tập")
                    .value(15)
                    .color("#f59e0b")
                    .build());
        }

        return distribution;
    }

    private List<AdminDashboardDTO.TopicPerformanceDTO> getTopicPerformance() {
        List<AdminDashboardDTO.TopicPerformanceDTO> performance = new ArrayList<>();

        // Get all vocab topics và tính toán thực tế
        List<Object[]> topicStats = vocabTopicRepository.findAll().stream()
                .map(topic -> {
                    long completed = userVocabProgressRepository.countByTopicIdAndIsCompletedTrue(topic.getId());
                    long inProgress = userVocabProgressRepository.countByTopicIdAndIsCompletedFalse(topic.getId());
                    long totalActivity = completed + inProgress;
                    return new Object[]{topic.getName(), completed, inProgress, totalActivity};
                })
                .filter(stat -> (Long)stat[3] > 0) // Chỉ lấy topics có hoạt động
                .sorted((a, b) -> Long.compare((Long)b[3], (Long)a[3])) // Sắp xếp theo tổng activity
                .limit(5)
                .toList();

        if (topicStats.isEmpty()) {
            // Dữ liệu mặc định nếu chưa có
            performance.add(AdminDashboardDTO.TopicPerformanceDTO.builder()
                    .topic("Chưa có dữ liệu")
                    .completed(0)
                    .inProgress(0)
                    .build());
        } else {
            for (Object[] stat : topicStats) {
                performance.add(AdminDashboardDTO.TopicPerformanceDTO.builder()
                        .topic((String) stat[0])
                        .completed(((Long) stat[1]).intValue())
                        .inProgress(((Long) stat[2]).intValue())
                        .build());
            }
        }

        return performance;
    }

    private List<AdminDashboardDTO.RecentActivityDTO> getRecentActivities() {
        List<AdminDashboardDTO.RecentActivityDTO> activities = new ArrayList<>();

        try {
            // Get recent forum posts
            List<Object[]> recentPosts = forumPostRepository.findTop5ByIsActiveTrueOrderByCreatedAtDesc().stream()
                    .limit(5)
                    .map(post -> new Object[]{
                            post.getId(),
                            post.getUser().getFullname(),
                            "đã đăng",
                            post.getTitle(),
                            formatTimeAgo(post.getCreatedAt())
                    })
                    .toList();

            for (Object[] activity : recentPosts) {
                activities.add(AdminDashboardDTO.RecentActivityDTO.builder()
                        .id((Long) activity[0])
                        .user((String) activity[1])
                        .action((String) activity[2])
                        .item((String) activity[3])
                        .time((String) activity[4])
                        .build());
            }
        } catch (Exception e) {
            log.warn("No recent activities found: {}", e.getMessage());
        }

        // Nếu không có hoạt động, thêm message mặc định
        if (activities.isEmpty()) {
            activities.add(AdminDashboardDTO.RecentActivityDTO.builder()
                    .id(0L)
                    .user("Hệ thống")
                    .action("")
                    .item("Chưa có hoạt động nào")
                    .time("Hôm nay")
                    .build());
        }

        return activities;
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        
        if (minutes < 1) {
            return "vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return hours + " giờ trước";
        } else {
            long days = minutes / 1440;
            return days + " ngày trước";
        }
    }
}
