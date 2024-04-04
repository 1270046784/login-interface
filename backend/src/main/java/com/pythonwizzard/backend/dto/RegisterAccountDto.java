package com.pythonwizzard.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterAccountDto {

    private final static String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @Pattern(regexp = USERNAME_REGEX)
    @Length(min = 2, max = 10)
    private String username;

    @Length(min = 4, max = 20)
    private String password;

    @Email
    private String email;

    private String verifyCode;

}
