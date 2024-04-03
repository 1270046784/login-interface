package com.cy.backend.controller;

import com.cy.backend.entity.RestBean;
import com.cy.backend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    private AuthorizeService service;

    @PostMapping("/valid-email")
    public RestBean<String> validateEmail(@Email @RequestParam("email") String email, HttpServletRequest request) {
        int status = service.sendValidateEmail(email, request.getRemoteAddr());
        if (status == 0) {
            return RestBean.success("邮件已发送");
        } else if (status == -1){
            return RestBean.failure(401, "邮件发送失败，请联系管理员");
        } else {
            return RestBean.failure(402, status + "秒后可再次获取验证码");
        }
    }

}
