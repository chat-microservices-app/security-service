package com.chatapp.securityservice.service;

import com.chatapp.securityservice.model.User;
import com.chatapp.securityservice.web.dto.Token;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

public interface JwtService {


    JwtClaimsSet generateJwtClaimSet(String subject, String scope, ChronoUnit unit, long duration);

    String generateAccessToken(Authentication authentication);


    String generateRefreshToken(Authentication authentication);


    Token generateToken(Authentication authentication);

    String generateTempAccessToken(String subject, String scope, ChronoUnit unit, long duration);

    Optional<User> validateToken(String token);
}
