package com.chatapp.securityservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @JsonProperty("username")
    private String username;


    @JsonProperty("password")
    private String password;

    @JsonProperty("authorities")
    private Set<GrantedAuthority> authorities;


    @Builder.Default
    @JsonProperty("accountNonExpired")
    private boolean accountNonExpired = true;

    @Builder.Default
    @JsonProperty("accountNonLocked")
    private boolean accountNonLocked = true;

    @Builder.Default
    @JsonProperty("credentialsNonExpired")
    private boolean credentialsNonExpired = true;

    @Builder.Default
    @JsonProperty("enabled")
    private boolean enabled = true;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }



}
