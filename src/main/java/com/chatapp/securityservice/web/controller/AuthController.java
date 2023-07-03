package com.chatapp.securityservice.web.controller;


import com.chatapp.securityservice.config.rest.RestProperties;
import com.chatapp.securityservice.service.AuthService;
import com.chatapp.securityservice.web.dto.LoginForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.dto.Token;
import com.chatapp.securityservice.web.dto.UserDetailsTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(RestProperties.ROOT + "/v1" +  RestProperties.AUTH.ROOT)
public class AuthController {

    private final AuthService authenticationService;

    @GetMapping(RestProperties.AUTH.LOGIN)
    public ResponseEntity<Token> login(@RequestBody LoginForm registrationRequest) {
        Token token = authenticationService.login(registrationRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, String.format("%s%s", RestProperties.TOKEN_PREFIX, token.accessToken()));
        return ResponseEntity.ok().headers(headers).body(token);
    }

    @PostMapping(RestProperties.AUTH.REGISTER)
    public ResponseEntity<Token> register(@RequestBody RegistrationForm registrationRequest) {
        return ResponseEntity.ok(authenticationService.register(registrationRequest));
    }

    @PutMapping(RestProperties.AUTH.REFRESH_TOKEN)
    public ResponseEntity<Token> refreshToken(@RequestBody Token refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

    @PostMapping(path = RestProperties.AUTH.CHECK_TOKEN,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserDetailsTransfer> checkToken(@RequestBody String token) {
        return ResponseEntity.ok(authenticationService.checkToken(token));
    }
}
