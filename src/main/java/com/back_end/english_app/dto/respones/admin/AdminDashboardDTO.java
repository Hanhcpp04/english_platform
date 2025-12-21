package com.back_end.english_app.dto.respones.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private List<StatCardDTO> stats;
    private List<UserGrowthDataDTO> userGrowthData;
    private List<ActivityDistributionDTO> activityDistribution;
    private List<TopicPerformanceDTO> topicPerformance;
    private List<RecentActivityDTO> recentActivities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatCardDTO {
        private String title;
        private String value;
        private String change;
        private Boolean isIncrease;
        private String icon;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGrowthDataDTO {
        private String month;
        private Integer users;
        private Integer active;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDistributionDTO {
        private String name;
        private Integer value;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicPerformanceDTO {
        private String topic;
        private Integer completed;
        private Integer inProgress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDTO {
        private Long id;
        private String user;
        private String action;
        private String item;
        private String time;
    }
}
