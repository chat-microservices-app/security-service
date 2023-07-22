package com.chatapp.securityservice.web.dto;


import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class AuthorizationForm  extends RegistrationForm {


    String roles;

    public AuthorizationForm(@NotNull String username, @NotNull String password, String firstName, String lastName, String email, Date dateOfBirth, String pictureUrl, String roles) {
        super(username, password, firstName, lastName, email, dateOfBirth, pictureUrl);
        this.roles = roles;
    }
}
