package com.cy.backend.service;

import com.cy.backend.entity.Account;
import com.cy.backend.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    private String senderEmail;  // 注入发送者

    @Resource
    private UserMapper userMapper;

    @Resource
    private MailSender mailSender;  // 提供了发送邮件接口

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
        Account account = userMapper.findAccountByNameOrEmails(username);
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
    public int sendValidateEmail(String email, String ipAddress) {
        String key = "email:" + ipAddress + ":" + email;  // 将当前邮件地址和ip地址存入redis

        // 判断到期时间，发邮件cd设置为60s
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            int expire = Math.toIntExact(Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L));
            if (expire > 120) {
                return expire - 120;  // 返回等待再次发送验证码剩余的时间
            }
        }

        Random random = new Random();
        int code = random.nextInt(900000) + 100000;  // 生成6位数字验证码
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(email);
        mailMessage.setSubject("来自Login Interface的验证邮件");
        mailMessage.setText("验证码是 " + code);

        try {
            mailSender.send(mailMessage);  // 捕获发送邮件可能产生的异常
            stringRedisTemplate.opsForValue().
                    set(key, String.valueOf(code), 3, TimeUnit.MINUTES);  // 验证码有效时间3min
            return 0;
        } catch (MailException e) {
            // 对 'printStackTrace()' 的调用可能应当替换为更可靠的日志
            return -1;
        }
    }
}
