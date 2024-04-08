package com.pythonwizzard.backend.config;

import com.alibaba.fastjson2.JSONObject;
import com.pythonwizzard.backend.entity.RestBean;
import com.pythonwizzard.backend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${frontend.host}")
    private String frontendHost;

    @Value("${create-table-on-startup}")
    private boolean createTableOnStartup;

    @Resource
    private AuthorizeService authorizeService;  // 登录服务

    @Resource
    private DataSource dataSource;  // 登录数据源

    /**
     * 登录过滤链路
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            PersistentTokenRepository tokenRepository
    ) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**")
                        .permitAll()  // 放行服务器端请求
                )  // 认证所有请求
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )  //登录
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onAuthenticationSuccess)
                )  // 登出
                .rememberMe(remember -> remember
                        .rememberMeParameter("remember")
                        .tokenRepository(tokenRepository)
                        .tokenValiditySeconds(7 * 24 * 3600)  // 记忆时限设置为7天
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(this::onAuthenticationFailure)
                )  // 登录异常
                .cors(cors -> cors
                        .configurationSource(this.configurationSource())
                )  // 允许跨域请求
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    /**
     * 建立数据库令牌仓库
     *
     * @return 数据库令牌仓库
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);  // 设置数据源
        jdbcTokenRepository.setCreateTableOnStartup(createTableOnStartup);  // 在建表时创建令牌仓库，仅第一次设置true
        return jdbcTokenRepository;
    }

    /**
     * 允许前端跨域请求
     *
     * @return 允许跨域的资源
     */
    private CorsConfigurationSource configurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("http://" + frontendHost + ":5173");
        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
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
     *
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
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        response.setCharacterEncoding("utf-8");
        if (request.getRequestURI().endsWith("/login")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
        } else if (request.getRequestURI().endsWith("/logout")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("成功登出")));
        }
    }

    /**
     * 登陆失败返回json
     */
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(400, exception.getMessage())));
    }
}
