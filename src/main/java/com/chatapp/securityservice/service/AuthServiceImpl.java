package com.chatapp.securityservice.service;

import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.dto.Token;
import com.chatapp.securityservice.web.dto.UserDetailsTransfer;
import com.chatapp.securityservice.web.mapper.UserMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerIllegalTokenException;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;


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
            if (exception instanceof IllegalArgumentException) {
                throw new IllegalArgumentException(exception.getMessage());
            } else if (exception instanceof FeignException) {
                throw exception;
            }
            throw new IllegalArgumentException(exception.getMessage());
        }
    }

    @Override
    public Token refreshToken(Token refreshTokenRequest) {
        try {
            Authentication authentication = refreshTokenAuthProvider
                    .authenticate(new BearerTokenAuthenticationToken(refreshTokenRequest.refreshToken()));
            return jwtService.generateToken(authentication);
        } catch (Exception ex) {
            if (ex instanceof FeignException) {
                throw ex;
            }
            log.error("the error is the following " + ex);
            throw new OAuthBearerIllegalTokenException(OAuthBearerValidationResult.newFailure("Error refreshing tokens"));
        }
    }

    @Override
    public Token login(LoginForm loginForm) {
        try {
            // authenticate the user using the authentication manager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password())
            );
            return jwtService.generateToken(authentication);
        } catch (Exception exception) {
            // if the user is locked, throw the exception
            if (exception instanceof LockedException) {
                String message = "The account is locked, please contact the administrator!";
                throw new LockedException(message);
            } else if (exception instanceof FeignException) {
                throw exception;
            } else if (exception instanceof InternalAuthenticationServiceException) {
                throw exception;
            } else {
                // if the user is not found. password or username is incorrect
                String message = "Username or password is incorrect";
                log.error("username not authorized ", exception);
                throw new BadCredentialsException(exception.getMessage());
            }
        }
    }

    @Override
    public UserDetailsTransfer checkToken(String token) {
        // check if token is valid and returns the user info from the token or throw the exception
        // by the token validation
        return userMapper.userToUserDetailsTransfer(jwtService.validateToken(token).orElseThrow());
    }
}
