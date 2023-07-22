package com.chatapp.securityservice.service;

import org.springframework.http.HttpHeaders;

public interface HeaderService {

    HttpHeaders getHeadersWithJwtToken(String username);
}
