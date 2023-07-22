package com.chatapp.securityservice.service;

import com.chatapp.securityservice.config.rest.RestProperties;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class HeaderServiceImpl implements HeaderService {

    @Value("${access-token.expiration-second}")
    private long accessTokenExpirationSecond;

    @Override
    public HttpHeaders getHeadersWithJwtToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        //create cookies
        Cookie accessTokenCookie = new Cookie("access-token", accessToken);

        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setMaxAge((int) accessTokenExpirationSecond);
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.AUTHORIZATION, RestProperties.TOKEN_PREFIX + accessToken);
        return headers;
    }
}
