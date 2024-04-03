package com.cy.backend.service;

import com.cy.backend.entity.Account;
import com.cy.backend.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Resource
    private UserMapper mapper;

    /**
     * 加载用户数据
     * @param username 用户名或邮箱
     * @return 用户数据
     * @throws UsernameNotFoundException 用户名不存在的异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("用户名为空");
        }
        Account account = mapper.findAccountByNameOrEmails(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    @Override
    public boolean sendValidateEmail(String email) {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;  // 生成6位数字验证码
        return false;
    }
}
