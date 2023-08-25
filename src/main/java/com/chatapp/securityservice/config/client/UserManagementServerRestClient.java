package com.chatapp.securityservice.config.client;

import com.chatapp.securityservice.config.rest.RestProperties;
import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.dto.UserDetailsTransfer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "${user-management-service.name}", url = "${user-management-service.url}")
public interface UserManagementServerRestClient {

    final String BASE_URL = RestProperties.ROOT + "/v1" + RestProperties.USER.USER;

    @PostMapping(path = BASE_URL + RestProperties.USER.REGISTER,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDetailsTransfer registerUser(@RequestBody RegistrationForm registrationForm,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostMapping(path = BASE_URL + RestProperties.USER.LOGIN,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDetailsTransfer login(@RequestBody LoginForm loginForm,
                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
    @GetMapping(path = BASE_URL + "/{userId}" + RestProperties.USER.DETAILS,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    UserDetailsTransfer loadUserDetails(@PathVariable(name = "userId") String userId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
