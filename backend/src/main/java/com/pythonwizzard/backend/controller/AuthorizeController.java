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

    @PostMapping("/validate-email")
    public RestBean<String> validateEmail(
            @Email @RequestParam("email") String email,
            HttpServletRequest request
    ) {
        String status = service.sendValidateEmail(email, request.getRemoteAddr());
        if (status == null) {
            return RestBean.success("邮件已发送");
        } else {
            return RestBean.failure(401, status);
        }
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(
//            @Valid @RequestBody RegisterAccountDto registerAccountDto,
            @Valid RegisterAccountDto registerAccountDto,
            HttpServletRequest request
    ) {
        String username = registerAccountDto.getUsername();
        String password = registerAccountDto.getPassword();
        String email = registerAccountDto.getEmail();
        String verifyCode = registerAccountDto.getVerifyCode();
        String ipAddress = request.getRemoteAddr();

        String status = service.validateRegister(username, password, email, verifyCode, ipAddress);
        if (status == null) {
            return RestBean.success("感谢您的注册，" + username + "先生/女士");
        } else {
            return RestBean.failure(401, status);
        }
    }

}
