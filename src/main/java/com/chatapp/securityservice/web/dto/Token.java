package com.chatapp.securityservice.web.dto;

public record Token(
        String accessToken,
        String refreshToken,

        String username
) {
}
