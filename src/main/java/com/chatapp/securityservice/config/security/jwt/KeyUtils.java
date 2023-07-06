package com.chatapp.securityservice.config.security.jwt;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@Getter
@Component
public class KeyUtils {

    @Value("${access-token.private-key}")
    private RSAPrivateKey accessTokenPrivateKey;

    @Value("${access-token.public-key}")
    private RSAPublicKey accessTokenPublicKey;

    @Value("${refresh-token.private-key}")
    private RSAPrivateKey refreshTokenPrivateKey;

    @Value("${refresh-token.public-key}")
    private RSAPublicKey refreshTokenPublicKey;

}
