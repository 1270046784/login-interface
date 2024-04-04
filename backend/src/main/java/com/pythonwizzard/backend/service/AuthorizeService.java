package com.pythonwizzard.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    String sendVerifyCode(
            String email,
            String ipAddress,
            boolean needEmailExisted
    );

    String register(
            String username,
            String password,
            String email,
            String verifyCode,
            String ipAddress
    );

    String validateEmail(
            String email,
            String verifyCode,
            String ipAddress
    );

    String resetPassword(
            String email,
            String newPassword
    );

}
