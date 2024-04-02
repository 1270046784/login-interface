package com.cy.backend.service;

import com.cy.backend.entity.Account;
import com.cy.backend.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeService implements UserDetailsService {

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
}
