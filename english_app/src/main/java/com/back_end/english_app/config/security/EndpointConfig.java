package com.back_end.english_app.config.security;


import org.springframework.http.HttpMethod;

import java.util.List;

public class EndpointConfig {
    public static final List<SecuredEndpoint> PUBLIC_ENDPOINTS = List.of(
    // cái nào k cần xác thực thì cho vào đây
            new SecuredEndpoint("/auth/register", HttpMethod.POST),
            new SecuredEndpoint("/auth/login", HttpMethod.POST),
            new SecuredEndpoint("/auth/refresh", HttpMethod.POST),
            new SecuredEndpoint("/badge/summary/{userId}", HttpMethod.GET),
            // Swagger endpoints
            new SecuredEndpoint("/v3/api-docs/**", HttpMethod.GET),
            new SecuredEndpoint("/swagger-ui/**", HttpMethod.GET),
            new SecuredEndpoint("/swagger-ui.html", HttpMethod.GET),
            //
            new SecuredEndpoint("/api/test-path", HttpMethod.GET)
    );

    public static final List<SecuredEndpoint> USER_ENDPOINTS = List.of(
    // của user cho vào đây
            // VocabController
            new SecuredEndpoint("/vocab/topic/*/words", HttpMethod.GET),
            new SecuredEndpoint("/vocab/complete", HttpMethod.POST),

            // VocabExerciseController - CHI TIẾT HƠN
            new SecuredEndpoint("/vocab/exercise/topics/*/exercise-types", HttpMethod.GET),
            new SecuredEndpoint("/vocab/exercise/exercise-types/*/topics/*/questions", HttpMethod.GET),
            new SecuredEndpoint("/vocab/exercise/questions/*/submit", HttpMethod.POST),
            new SecuredEndpoint("/vocab/exercise/topics/*/progress", HttpMethod.GET),
            new SecuredEndpoint("/vocab/exercise/exercise-types/*/submit-batch", HttpMethod.POST),
            new SecuredEndpoint("/vocab/exercise/exercise-types/*/progress", HttpMethod.DELETE),

            // VocabStatsController
            new SecuredEndpoint("/vocab/stats/*", HttpMethod.GET),
            new SecuredEndpoint("/vocab/topics", HttpMethod.GET),
            new SecuredEndpoint("/vocab/topics/*", HttpMethod.GET),
            new SecuredEndpoint("/vocab/topics/progress/*", HttpMethod.GET)
    );

    public static final List<SecuredEndpoint> ADMIN_ENDPOINTS = List.of(
    // cái nào của admin thì cho vào đây


    );
    public static final List<SecuredEndpoint> AUTHENTICATED_ENDPOINTS=List.of(
//  Nếu một API cần xác thực nhưng không cần phân quyền cụ thể, bạn đưa vào AUTHENTICATED_ENDPOINTS.
//  Ví dụ: profile cá nhân, đổi mật khẩu, logout, xem thông tin riêng của mình.
            new SecuredEndpoint("/auth/me", HttpMethod.GET),
            new SecuredEndpoint("/auth/logout", HttpMethod.POST)
    );
}
