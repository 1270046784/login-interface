package com.pythonwizzard.backend.controller;

import com.pythonwizzard.backend.dto.RegisterAccountDto;
import com.pythonwizzard.backend.entity.RestBean;
import com.pythonwizzard.backend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    private AuthorizeService service;

    @PostMapping("/send-to-existed-email")
    public RestBean<String> sendToExistedEmail(
            @Email @RequestParam("email") String email,
            HttpServletRequest request
    ) {
        String status = service.sendVerifyCode(email, request.getRemoteAddr(), true);
        if (status == null) {
            return RestBean.success("邮件已发送");
        } else {
            return RestBean.failure(401, status);
        }
    }

    @PostMapping("/send-to-nonexistent-email")
    public RestBean<String> sendToNonexistentEmail(
            @Email @RequestParam("email") String email,
            HttpServletRequest request
    ) {
        String status = service.sendVerifyCode(email, request.getRemoteAddr(), false);
        if (status == null) {
            return RestBean.success("邮件已发送");
        } else {
            return RestBean.failure(401, status);
        }
    }

    @PostMapping("/register")
    public RestBean<String> registerAccount(
            @Valid RegisterAccountDto registerAccountDto,
            HttpServletRequest request
    ) {
        String username = registerAccountDto.getUsername();
        String password = registerAccountDto.getPassword();
        String email = registerAccountDto.getEmail();
        String verifyCode = registerAccountDto.getVerifyCode();
        String ipAddress = request.getRemoteAddr();

        String status = service.register(username, password, email, verifyCode, ipAddress);
        if (status == null) {
            return RestBean.success("感谢您的注册，" + username + "先生/女士");
        } else {
            return RestBean.failure(401, status);
        }
    }

    @PostMapping("/validate-email")
    public RestBean<String> validateEmail(
            @Valid RegisterAccountDto registerAccountDto,
            HttpServletRequest request
    ) {
        String email = registerAccountDto.getEmail();
        String verifyCode = registerAccountDto.getVerifyCode();
        String ipAddress = request.getRemoteAddr();

        String status = service.validateEmail(email, verifyCode, ipAddress);
        if (status == null) {
            return RestBean.success("已成功校验，请重置密码");
        } else {
            return RestBean.failure(401, status);
        }
    }

    @PostMapping("/reset-password")
    public RestBean<String> resetPassword(
            @Valid RegisterAccountDto registerAccountDto
    ) {
        String email = registerAccountDto.getEmail();
        String newPassword = registerAccountDto.getPassword();

        String status = service.resetPassword(email, newPassword);
        if (status == null) {
            return RestBean.success("成功重置密码，返回登陆界面");
        } else {
            return RestBean.failure(401, status);
        }
    }

}
