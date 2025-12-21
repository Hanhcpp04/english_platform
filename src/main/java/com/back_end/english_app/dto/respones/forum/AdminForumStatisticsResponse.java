package com.back_end.english_app.dto.respones.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminForumStatisticsResponse {
    private Long totalPosts;
    private Long totalActivePosts;
    private Long totalDeletedPosts;
    private Long totalComments;
    private Long totalActiveComments;
    private Long totalDeletedComments;
    private Long totalLikes;
    private Long totalViews;
    private Long postsToday;
    private Long commentsToday;
}
