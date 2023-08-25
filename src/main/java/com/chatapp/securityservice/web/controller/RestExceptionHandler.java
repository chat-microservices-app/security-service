package com.chatapp.securityservice.web.controller;


import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerIllegalTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = {OAuthBearerIllegalTokenException.class})
    protected ResponseEntity<Object> oF(Exception ex, WebRequest request) {

        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.valueOf(498), request);

    }


    @ExceptionHandler(value = {LockedException.class})
    protected ResponseEntity<Object> handleLockedException(Exception ex, WebRequest request) {


        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);

    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    protected ResponseEntity<Object> handleBadCredentials(Exception ex, WebRequest request) {

        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);

    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(Exception ex, WebRequest request) {

        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.CONFLICT, request);

    }


    @ExceptionHandler(value = {FeignException.class, FeignException.FeignClientException.class, InternalAuthenticationServiceException.class})
    protected ResponseEntity<Object> handleFeignException(Exception ex, WebRequest request) {

        // parse the exception message to get the status code
        // each word is separated by a space and wrapped in []
        String[] message = ex.getMessage().split(" ");

        log.info("message: " + Arrays.toString(message));

        //first element is the status code
        int statusCode = Integer.parseInt(message[0].replace("[", "").replace("]", ""));

        // last element is the message
        String errorMessage =
                ex
                        .getMessage()
                        .substring(
                                ex.getMessage().lastIndexOf("[") + 1,
                                ex.getMessage().lastIndexOf("]"));

        return handleExceptionInternal(ex, errorMessage,
                new HttpHeaders(), HttpStatus.valueOf(statusCode), request);
    }
}
