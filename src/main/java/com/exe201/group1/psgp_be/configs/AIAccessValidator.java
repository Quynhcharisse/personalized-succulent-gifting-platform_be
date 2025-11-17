package com.exe201.group1.psgp_be.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AIAccessValidator {

    private final AIAccessProperties properties;

    public boolean isValid(String token) {
        return StringUtils.hasText(token)
                && StringUtils.hasText(properties.getApiToken())
                && token.equals(properties.getApiToken());
    }

    public String extractToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }
        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return authorizationHeader;
    }
}

