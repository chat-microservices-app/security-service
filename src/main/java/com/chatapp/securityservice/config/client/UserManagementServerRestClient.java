package com.chatapp.securityservice.config.client;

import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.dto.UserDetailsTransfer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "user-management-service")
public interface UserManagementServerRestClient {

    final String BASE_URL = "/api/v1/users";

    @PostMapping(path = BASE_URL + "/register", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDetailsTransfer registerUser(@RequestBody RegistrationForm registrationForm, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostMapping(path = BASE_URL + "/login", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDetailsTransfer login(@RequestBody LoginForm loginForm, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @GetMapping(path = BASE_URL + "/{userId}/details", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDetailsTransfer loadUserDetails(@PathVariable(name = "userId") String userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
