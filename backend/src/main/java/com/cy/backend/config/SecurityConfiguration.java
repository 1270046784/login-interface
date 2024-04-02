package com.cy.backend.config;

import com.alibaba.fastjson2.JSONObject;
import com.cy.backend.entity.RestBean;
import com.cy.backend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    private AuthorizeService authorizeService;

    /**
     * 登录过滤链路
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )  // 认证所有请求
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )  //登录
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                )  // 登出
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(this::onAuthenticationFailure)
                )  // 登录异常
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    /**
     * 登录管理
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                security.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(authorizeService);
        return authenticationManagerBuilder.build();
    }

    /**
     * 对密码进行编码
     * @return 编码后的密码
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 登录成功返回json
     */
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
    }

    /**
     * 登陆失败返回json
     */
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
    ) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
    }
}
