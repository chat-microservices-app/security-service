package com.chatapp.securityservice.service;


import com.chatapp.securityservice.config.rest.RestProperties;
import com.chatapp.securityservice.model.User;
import com.chatapp.securityservice.web.dto.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerIllegalTokenException;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Log4j2
@RequiredArgsConstructor
@Service
// get values from the application.properties file
public class JwtServiceImpl implements JwtService {


    @Value("${access-token.expiration-second}")
    private long accessTokenExpirationSecond;

    @Value("${refresh-token.expiration-second}")
    private long refreshTokenExpirationSecond;

    private final JwtEncoder accessTokenEncoder;


    private final JwtDecoder accessTokenDecoder;
    private JwtEncoder refreshTokenEncoder;

    @Autowired
    @Qualifier("jwtRefreshTokenEncoder")
    protected void setRefreshTokenEncoder(JwtEncoder refreshTokenEncoder) {
        this.refreshTokenEncoder = refreshTokenEncoder;
    }

    @Override
    public JwtClaimsSet generateJwtClaimSet(String subject, String scope, ChronoUnit unit, long duration) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
                .issuer("chatapp.com")
                .issuedAt(now)
                .expiresAt(now.plus(duration, unit))
                .subject(subject)
                .claim("roles", scope)
                .build();
    }

    @Override
    public String generateAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::new)
                .collect(joining(" "));
        log.debug("roleScope: {}" , scope);
        JwtClaimsSet claimsSet = generateJwtClaimSet(authentication.getName(), scope, ChronoUnit.SECONDS,
                accessTokenExpirationSecond);
        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    @Override
    public String generateRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(" "));
        JwtClaimsSet claimsSet = generateJwtClaimSet(authentication.getName(), scope, ChronoUnit.SECONDS,
                refreshTokenExpirationSecond);
        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }



    @Override
    public Token generateToken(Authentication authentication) {

        if(!(authentication.getPrincipal() instanceof UserDetails user)) {

            return new Token(
                    generateAccessToken(authentication),
                    regenerateRefreshToken(authentication),
                    authentication.getName()
            );
        }
        return new Token(
                generateAccessToken(authentication),
                regenerateRefreshToken(authentication),
                authentication.getName()
        );
    }

    @Override
    public String generateTempAccessToken(String subject, String scope, ChronoUnit unit, long duration) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = generateJwtClaimSet(subject, scope, unit, duration);
        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    @Override
    public Optional<User> validateToken(String token) {
        try {
            if(token.startsWith(RestProperties.TOKEN_PREFIX)) token = token.substring(7);
            Jwt jwt = accessTokenDecoder.decode(token);
            if (Objects.requireNonNull(jwt.getExpiresAt()).isAfter(Instant.now())) {
                return Optional.of(getUserFromToken(token));
            } else {
                throw new OAuthBearerIllegalTokenException(OAuthBearerValidationResult.newFailure("Token expired"));
            }
        } catch (JwtException e) {
            //TODO add better exception handling
            throw new OAuthBearerIllegalTokenException(OAuthBearerValidationResult.newFailure(e.getMessage()));
        }
    }


    protected User getUserFromToken(String token) {
        Jwt jwt = accessTokenDecoder.decode(token);
        Set<GrantedAuthority> authorities = Arrays.stream(jwt.getClaimAsString("roles").split(" "))
                .map(role -> (GrantedAuthority) () -> role)
                .collect(Collectors.toSet());
        return User.builder().username(jwt.getSubject()).authorities(authorities).build();
    }


    protected String regenerateRefreshToken(Authentication authentication) {
        String refreshToken;
        if (authentication.getCredentials() instanceof Jwt jwt) {
            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(Instant.now(), expiresAt);
            long daysUntilExpired = duration.toDays();
            if (daysUntilExpired < 1) { // generate new refresh token if the current one is about to expire in 1 day
                refreshToken = generateRefreshToken(authentication);
            } else {
                refreshToken = jwt.getTokenValue();
            }
        } else {
            refreshToken = generateRefreshToken(authentication);
        }
        return refreshToken;
    }
}
