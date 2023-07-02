package com.chatapp.securityservice.service;

import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDetails createUser(RegistrationForm registrationForm);

    UserDetails loginUser(LoginForm loginForm);

}
