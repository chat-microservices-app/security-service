package com.chatapp.securityservice.web.controller;


import com.chatapp.securityservice.config.rest.RestProperties;
import com.chatapp.securityservice.service.AuthService;
import com.chatapp.securityservice.service.HeaderService;
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
@RequestMapping(RestProperties.ROOT + "/v1" + RestProperties.AUTH.ROOT)
public class AuthController {


    private final AuthService authenticationService;
    private final HeaderService headerService;

    @PostMapping(RestProperties.AUTH.LOGIN)
    public ResponseEntity<Token> login(@RequestBody LoginForm registrationRequest) {
        Token token = authenticationService.login(registrationRequest);
        HttpHeaders headers = headerService.getHeadersWithJwtToken(token.accessToken());
        return ResponseEntity.ok().headers(headers).body(token);
    }

    @PostMapping(RestProperties.AUTH.REGISTER)
    public ResponseEntity<Token> register(@RequestBody RegistrationForm registrationRequest) {
        Token token = authenticationService.register(registrationRequest);
        return ResponseEntity.ok().headers(headerService.getHeadersWithJwtToken(token.accessToken())).body(token);
    }

    @PutMapping(RestProperties.AUTH.REFRESH_TOKEN)
    public ResponseEntity<Token> refreshToken(@RequestBody Token refreshTokenRequest) {
        Token token = authenticationService.refreshToken(refreshTokenRequest);
        HttpHeaders headers = headerService.getHeadersWithJwtToken(token.accessToken());
        return ResponseEntity.ok().headers(headers).body(token);
    }

    @PostMapping(path = RestProperties.AUTH.CHECK_TOKEN,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserDetailsTransfer> checkToken(@RequestBody String token) {
        return ResponseEntity.ok(authenticationService.checkToken(token));
    }
}
