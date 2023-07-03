package com.chatapp.securityservice.service;

import com.chatapp.securityservice.config.client.UserManagementServerRestClient;
import com.chatapp.securityservice.config.rest.RestProperties;
import com.chatapp.securityservice.enums.Role;
import com.chatapp.securityservice.web.dto.AuthorizationForm;
import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserManagementServerRestClient userManagementServerRestClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);
        return userMapper.userDetailsTransferToUser(userManagementServerRestClient.loadUserDetails(username,
                RestProperties.TOKEN_PREFIX + generateTempAccessToken(username)));
    }

    @Override
    public UserDetails createUser(RegistrationForm registrationForm) {
        String encodedPassword = passwordEncoder.encode(registrationForm.getPassword());
        AuthorizationForm authorizationForm = userMapper
                .registrationFormToAuthorizationForm(registrationForm);
        authorizationForm.setPassword(encodedPassword);
        return userMapper.userDetailsTransferToUser(userManagementServerRestClient
                .registerUser(authorizationForm,
                        RestProperties.TOKEN_PREFIX + generateTempAccessToken(registrationForm.getUsername())));
    }

    @Override
    public UserDetails loginUser(LoginForm loginForm) {
        LoginForm loginFormWithEncodedPassword = new LoginForm(
                loginForm.username(), passwordEncoder.encode(loginForm.password()));
        return userMapper.userDetailsTransferToUser(userManagementServerRestClient
                .login(loginFormWithEncodedPassword, RestProperties.TOKEN_PREFIX +
                        generateTempAccessToken(loginForm.username())));
    }

    protected String generateTempAccessToken(String username) {
        return jwtService.generateTempAccessToken(username, Role.PREFIX.getLabel() + Role.SERVICE.getLabel() + " " +
                        Role.Permission.getAllPermissionsByRoleType(Role.SERVICE)
                                .stream()
                                .map(String::strip)
                                .collect(Collectors.joining(" ")),
                ChronoUnit.MINUTES, 4);
    }

}
