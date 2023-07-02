package com.chatapp.securityservice.service;

import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.dto.Token;
import com.chatapp.securityservice.web.dto.UserDetailsTransfer;
import com.chatapp.securityservice.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@RequiredArgsConstructor
@Log4j2
@Service
public class AuthServiceImpl implements AuthService {


    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;
    private JwtAuthenticationProvider refreshTokenAuthProvider;

    private final JwtService jwtService;

    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    private void setJwtAuthenticationProvider(JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.refreshTokenAuthProvider = jwtAuthenticationProvider;
    }

    @Override
    public Token register(RegistrationForm registrationForm) {
        userService.createUser(registrationForm);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registrationForm.getUsername(), registrationForm.getPassword()));
            return jwtService.generateToken(authentication);
        } catch (Exception exception) {
            throw new NoSuchElementException("Username or password is incorrect");
        }
    }

    @Override
    public Token refreshToken(Token refreshTokenRequest) {
        try {
            Authentication authentication = refreshTokenAuthProvider
                    .authenticate(new BearerTokenAuthenticationToken(refreshTokenRequest.refreshToken()));

            return jwtService.generateToken(authentication);
        } catch (Exception ex) {
            log.error("the error is the following " + ex);
            throw new NoSuchElementException("User not found");
        }
    }

    @Override
    public Token login(LoginForm loginForm) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password())
            );
            return jwtService.generateToken(authentication);
        } catch (Exception exception) {
            if (exception instanceof org.springframework.security.authentication.LockedException) {
                String message = "The account is locked, please contact the administrator!";
                throw new LockedException(message);
            } else {
                String message = "Username or password is incorrect";
                log.error("username not authorized " + exception);
                throw new IllegalArgumentException(message);
            }
        }
    }

    @Override
    public UserDetailsTransfer checkToken(String token) {
        // check if token is valid and returns the user info from the token
        return userMapper.userToUserDetailsTransfer(jwtService.validateToken(token).orElseThrow(
                () -> new IllegalArgumentException("Something went wrong")
        ));
    }
}
