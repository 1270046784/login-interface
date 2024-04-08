package com.pythonwizzard.backend.interceptor;

import com.pythonwizzard.backend.entity.user.NormalUser;
import com.pythonwizzard.backend.dao.AccountDao;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Resource
    private AccountDao accountDao;

    /**
     * 设置拦截器
     */
    @Override
    public boolean preHandle(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull Object handler
    ) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        try {
            User user = (User) authentication.getPrincipal();
            String username = user.getUsername();
            NormalUser normalUser = accountDao.findNormalUserByNameOrEmails(username);
            request.getSession().setAttribute("user", normalUser);  // 将用户放入session
            return true;
        } catch (Exception e) {  // 如果未登录则principal是字符串类
            return false;
        }
    }
}
