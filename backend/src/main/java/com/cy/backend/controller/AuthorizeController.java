package com.cy.backend.controller;

import com.cy.backend.entity.RestBean;
import com.cy.backend.service.AuthorizeService;
import jakarta.annotation.Resource;
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
    public RestBean<String> validateEmail(@Email @RequestParam("email") String email) {
        if (service.sendValidateEmail(email)) {
            return RestBean.success("邮件已发送");
        } else {
            return RestBean.failure(400, "邮件发送失败");
        }
    }

}
