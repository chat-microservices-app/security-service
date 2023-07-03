package com.chatapp.securityservice.web.mapper;


import com.chatapp.securityservice.enums.Role;
import com.chatapp.securityservice.model.User;
import com.chatapp.securityservice.web.dto.AuthorizationForm;
import com.chatapp.securityservice.web.dto.RegistrationForm;
import com.chatapp.securityservice.web.dto.UserDetailsTransfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "authorities", expression = "java(map(user.getAuthorities()))")
    UserDetailsTransfer userToUserDetailsTransfer(User user);


    @Mapping(target = "authorities", expression = "java(from(userDetailsTransfer.getAuthorities()))")
    User userDetailsTransferToUser(UserDetailsTransfer userDetailsTransfer);


    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", expression = "java(setUpRoles())")
    AuthorizationForm registrationFormToAuthorizationForm(RegistrationForm registrationForm);


    default String setUpRoles() {
        return Role.PREFIX.getLabel() + Role.USER.getLabel() + " " + Role.Permission.getAllPermissionsByRoleType(Role.USER)
                .stream()
                .map(String::strip)
                .collect(Collectors.joining(" "));
    }

    default Set<GrantedAuthority> from(Set<String> authorities) {
        if (authorities == null) {
            return null;
        }
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    default Set<String> map(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            return null;
        }
        return authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }


}
