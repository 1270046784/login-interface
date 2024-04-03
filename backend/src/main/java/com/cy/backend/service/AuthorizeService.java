package com.cy.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    // 将邮件地址和ip地址作为调用接口的凭据
    // 返回值 0 代表正确发送邮件，返回值 -1 代表出现异常，返回值 正数 代表再次发送验证码需要等待的时间
    int sendValidateEmail(String email, String ipAddress);
}
