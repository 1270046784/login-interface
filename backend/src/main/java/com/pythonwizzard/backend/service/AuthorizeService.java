package com.pythonwizzard.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    String sendValidateEmail(
            String email,
            String ipAddress
    );

    String validateRegister(
            String username,
            String password,
            String email,
            String verifyCode,
            String ipAddress
    );

}
