package com.chatapp.securityservice.web.dto;


import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class AuthorizationForm  extends RegistrationForm {


    String roles;

    public AuthorizationForm(String username, String password, String firstName, String lastName, String email, Date date, String roles) {
        super(username, password, firstName, lastName, email, date);
        this.roles = roles;
    }
}
