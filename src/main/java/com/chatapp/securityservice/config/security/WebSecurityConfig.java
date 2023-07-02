package com.chatapp.securityservice.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;


@RequiredArgsConstructor
@EnableGlobalAuthentication
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class WebSecurityConfig {

    private final String SPRING_ACTUATOR_PATH = "/actuator";
    private final String ALLOW_ALL_ENDPOINTS = "/**";

    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorizeRequestRegistry ->
                authorizeRequestRegistry
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(allowedGetEndpoints()).permitAll()
                        .anyRequest()
                        .authenticated()
        );

        //exception handler for users not authorized to access the resource
        http.exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
        );

        // for every request, the user must be authenticated
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // add the jwt filter before the UsernamePasswordAuthenticationFilter to validate the token
        http.httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                        httpSecurityOAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults())
                );


        http.headers(httpSecurityHeadersConfigurer ->
                httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        return http.build();
    }

    private String[] allowedGetEndpoints() {
        return new String[]{
                SPRING_ACTUATOR_PATH + ALLOW_ALL_ENDPOINTS,
                "/api/v1/auth" + ALLOW_ALL_ENDPOINTS,
                "/swagger-ui.html" + ALLOW_ALL_ENDPOINTS,
                "/swagger-ui" + ALLOW_ALL_ENDPOINTS,
                "/v3/api-docs" + ALLOW_ALL_ENDPOINTS,
                "/error" + ALLOW_ALL_ENDPOINTS,
                "/api-docs" + ALLOW_ALL_ENDPOINTS,
        };
    }


}
