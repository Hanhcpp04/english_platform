package com.back_end.english_app.config.security;


import org.springframework.http.HttpMethod;

import java.util.List;

public class EndpointConfig {
    public static final List<SecuredEndpoint> PUBLIC_ENDPOINTS = List.of(
    // cái nào k cần xác thực thì cho vào đây
            new SecuredEndpoint("/auth/register", HttpMethod.POST),
            new SecuredEndpoint("/auth/login", HttpMethod.POST),
            new SecuredEndpoint("/auth/refresh", HttpMethod.POST),
            new SecuredEndpoint("/71dad410-ef40-4ae6-920b-4a0b82f6d864_quality_icons.webp",HttpMethod.GET),

            // Badge public endpoints - xem badges của users khác
            new SecuredEndpoint("/badge/summary/*", HttpMethod.GET),
            new SecuredEndpoint("/badge/all/*", HttpMethod.GET),
            new SecuredEndpoint("/badge/recent/*", HttpMethod.GET),

            // Swagger endpoints
            new SecuredEndpoint("/v3/api-docs/**", HttpMethod.GET),
            new SecuredEndpoint("/swagger-ui/**", HttpMethod.GET),
            new SecuredEndpoint("/swagger-ui.html", HttpMethod.GET),
            //
            new SecuredEndpoint("/api/test-path", HttpMethod.GET)
    );

    public static final List<SecuredEndpoint> USER_ENDPOINTS = List.of(
    // của user cho vào đây
            // Badge authenticated endpoints - xem và quản lý badges của chính mình
            new SecuredEndpoint("/badge/progress", HttpMethod.GET),
            new SecuredEndpoint("/badge/progress/*", HttpMethod.GET),
            new SecuredEndpoint("/badge/earned", HttpMethod.GET),
            new SecuredEndpoint("/badge/check", HttpMethod.POST),
            // VocabController
            new SecuredEndpoint("/vocab/topic/*/words", HttpMethod.GET),
            new SecuredEndpoint("/vocab/complete", HttpMethod.POST),

            // VocabExerciseController
            new SecuredEndpoint("/vocab/exercise/topics/*/exercise-types", HttpMethod.GET),
            new SecuredEndpoint("/vocab/exercise/exercise-types/*/topics/*/questions", HttpMethod.GET),
            new SecuredEndpoint("/vocab/exercise/questions/*/submit", HttpMethod.POST),
            new SecuredEndpoint("/vocab/exercise/topics/*/progress", HttpMethod.GET),
            new SecuredEndpoint("/vocab/exercise/exercise-types/*/submit-batch", HttpMethod.POST),
            new SecuredEndpoint("/vocab/exercise/exercise-types/*/progress", HttpMethod.DELETE),
            new SecuredEndpoint("/vocab/exercise/history", HttpMethod.GET),
            new SecuredEndpoint("/vocab/exercise/reset", HttpMethod.DELETE),
            new SecuredEndpoint("/vocab/exercise/accuracy", HttpMethod.GET),

            // VocabStatsController
            new SecuredEndpoint("/vocab/stats/*", HttpMethod.GET),
            new SecuredEndpoint("/vocab/topics", HttpMethod.GET),
            new SecuredEndpoint("/vocab/topics/*", HttpMethod.GET),
            new SecuredEndpoint("/vocab/topics/progress/*", HttpMethod.GET),
            // Grammar stats
            new SecuredEndpoint("/grammar/stats/*", HttpMethod.GET),
            new SecuredEndpoint("/grammar/topics/progress/*",HttpMethod.GET),
            new SecuredEndpoint("/grammar/topics/*/progress/*",HttpMethod.GET),
            // grammar detail
            new SecuredEndpoint("/grammar/topics/*/lessons",HttpMethod.GET),
            new SecuredEndpoint("/grammar/lessons/complete",HttpMethod.POST),
            // grammar exercise
            new SecuredEndpoint("/grammar/exercises/questions", HttpMethod.GET),
            new SecuredEndpoint("/grammar/exercises/submit", HttpMethod.POST),
            new SecuredEndpoint("/grammar/exercises/history", HttpMethod.GET),
            new SecuredEndpoint("/grammar/exercises/types", HttpMethod.GET),
            new SecuredEndpoint("/grammar/exercises/reset", HttpMethod.DELETE),
            new SecuredEndpoint("/grammar/exercises/accuracy", HttpMethod.GET),
            // writting
            new SecuredEndpoint("/writing/topics", HttpMethod.GET),
            new SecuredEndpoint("/writing/topics/*/tasks", HttpMethod.GET),
            new SecuredEndpoint("/writing/tasks/*/prompts", HttpMethod.GET),
            // Writing submit (AI grading)
            new SecuredEndpoint("/writing/submit", HttpMethod.POST),
            //forum
            new SecuredEndpoint("/forum/posts",HttpMethod.GET),
            new SecuredEndpoint("/forum/posts/recent",HttpMethod.GET),
            new SecuredEndpoint("/forum/post/detail/*",HttpMethod.GET),
            new SecuredEndpoint("/forum/post/create/*",HttpMethod.POST),
            new SecuredEndpoint("/forum/post/update/*",HttpMethod.PUT),
            new SecuredEndpoint("/forum/post/delete/*",HttpMethod.DELETE),
            // Forum comment endpoints
            new SecuredEndpoint("/forum/posts/*/comments", HttpMethod.GET),
            new SecuredEndpoint("/forum/posts/*/comments", HttpMethod.POST),
            new SecuredEndpoint("/forum/comments/*", HttpMethod.PUT),
            new SecuredEndpoint("/forum/comments/*", HttpMethod.DELETE),
            // Forum like endpoints
            new SecuredEndpoint("/forum/posts/*/like", HttpMethod.POST),
            new SecuredEndpoint("/forum/comments/*/like", HttpMethod.POST)


    );

    public static final List<SecuredEndpoint> ADMIN_ENDPOINTS = List.of(
    // cái nào của admin thì cho vào đây
            //user
            new SecuredEndpoint("/admin-users/getAll", HttpMethod.GET),
            new SecuredEndpoint("/admin-users/update-role/{id}/{role}", HttpMethod.PUT),
            new SecuredEndpoint("/admin-users/delete-restore/{id}/{status}", HttpMethod.PUT), //status: delete hoặc restore

            //level
            new SecuredEndpoint("/admin-levels/getAll", HttpMethod.GET), //level ko phân trang đâu nha
            new SecuredEndpoint("/admin-levels/create", HttpMethod.POST),
            new SecuredEndpoint("/admin-levels/update/{levelNumber}", HttpMethod.PUT),

            //badge
            new SecuredEndpoint("/admin-badges/getAll", HttpMethod.GET),
            new SecuredEndpoint("/admin-badges/create", HttpMethod.POST),
            new SecuredEndpoint("/admin-badges/update/{id}", HttpMethod.PUT),
            new SecuredEndpoint("/admin-badges/delete-restore/{id}/{status}", HttpMethod.PUT), //status: delete hoặc restore

            //vocab topic
            new SecuredEndpoint("/admin-vocab/topics/getAll", HttpMethod.GET),
            new SecuredEndpoint("/admin-vocab/topics/create", HttpMethod.POST),
            new SecuredEndpoint("/admin-vocab/topics/update/{id}", HttpMethod.PUT),
            new SecuredEndpoint("/admin-vocab/topics/delete-restore/{id}/{status}", HttpMethod.PUT), //status: delete hoặc restore

            //vocab word
            new SecuredEndpoint("/admin-vocab/words/getAll", HttpMethod.GET),
            new SecuredEndpoint("/admin-vocab/words/create", HttpMethod.POST),
            new SecuredEndpoint("/admin-vocab/words/update/{id}", HttpMethod.PUT),
            new SecuredEndpoint("/admin-vocab/words/delete-restore/{id}/{status}", HttpMethod.PUT) //status: delete hoặc restore
            );
    public static final List<SecuredEndpoint> AUTHENTICATED_ENDPOINTS=List.of(
//  Nếu một API cần xác thực nhưng không cần phân quyền cụ thể, bạn đưa vào AUTHENTICATED_ENDPOINTS.
//  Ví dụ: profile cá nhân, đổi mật khẩu, logout, xem thông tin riêng của mình.
            new SecuredEndpoint("/auth/me", HttpMethod.GET),
            new SecuredEndpoint("/auth/logout", HttpMethod.POST),
            
            // Dashboard endpoints
            new SecuredEndpoint("/dashboard/summary", HttpMethod.GET),
            new SecuredEndpoint("/dashboard/summary/*", HttpMethod.GET)
    );
}
