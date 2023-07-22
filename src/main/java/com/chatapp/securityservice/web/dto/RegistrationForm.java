package com.chatapp.securityservice.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@EqualsAndHashCode
@NoArgsConstructor
@Data
public class RegistrationForm {
    @NotNull @NotEmpty @NotBlank @Length(min = 5, max = 100,
            message = "Username must be between 5 and 100 characters")
    String username;

    @NotNull @NotEmpty @NotBlank
    String password;

    @Length(max = 250, message = "First name must be less than 100 characters")
    String firstName;


    @Length(max = 250, message = "Last name must be less than 100 characters")
    String lastName;

    @Email
    String email;

    Date dateOfBirth;

    String pictureUrl;

    public RegistrationForm(@NotNull String username, @NotNull String password, String firstName, String lastName, String email, Date dateOfBirth, String pictureUrl) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.pictureUrl = pictureUrl;
    }
}

