package com.chatapp.securityservice.service;

import com.chatapp.securityservice.model.User;
import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.dto.Token;
import com.chatapp.securityservice.web.dto.UserDetailsTransfer;

public interface AuthService {

    Token register(RegistrationForm registrationForm);

    Token refreshToken(Token refreshTokenRequest);

    Token login(LoginForm loginForm);


    UserDetailsTransfer checkToken(String token);
}
