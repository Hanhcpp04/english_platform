package com.back_end.english_app.config.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
@Builder
public class SecuredEndpoint {
    private final String url;
    private final HttpMethod httpMethod;
}
