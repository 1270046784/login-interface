package com.pythonwizzard.backend.service;

import com.pythonwizzard.backend.entity.Account;
import com.pythonwizzard.backend.mapper.AccountMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    private final static String EXCEPTION_MESSAGE = "发生错误，请联系管理员";
    private final static String WRONG_VERIFY_CODE_MESSAGE = "验证码错误，请重新输入";
    private final static String VERIFY_CODE_EXPIRE_MESSAGE = "验证码失效，请重新获取";

    @Value("${spring.mail.username}")
    private String senderEmail;  // 注入发送者

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private MailSender mailSender;  // 提供了发送邮件接口

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 与SecurityConfiguration中的编码器解耦，避免循环引用
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        Account account = accountMapper.findAccountByNameOrEmails(username);
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
    public String sendVerifyCode(String email, String ipAddress, boolean needEmailExisted) {
        String key = "email:" + ipAddress + ":" + email;  // 将当前邮件地址和ip地址存入redis

        if (needEmailExisted && accountMapper.findAccountByNameOrEmails(email) == null) {
            return "账号不存在";
        }
        if (!needEmailExisted && accountMapper.findAccountByNameOrEmails(email) != null) {
            return "账号已注册";
        }

        // 判断到期时间，发邮件cd设置为60s
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            int expire = Math.toIntExact(
                    Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS))
                            .orElse(0L)
            );
            if (expire > 120) {
                return (expire - 120) + "秒后可再次获取";  // 返回等待再次发送验证码剩余的时间
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
            return null;
        } catch (MailException e) {
            // 对 'printStackTrace()' 的调用可能应当替换为更可靠的日志
            return EXCEPTION_MESSAGE;
        }
    }


    @Override
    public String register(
            String username,
            String password,
            String email,
            String verifyCode,
            String ipAddress
    ) {
        String key = "email:" + ipAddress + ":" + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            String realVerifyCode = stringRedisTemplate.opsForValue().get(key);  // 获取实际发送的验证码
            if (verifyCode.length() != 6) {
                return WRONG_VERIFY_CODE_MESSAGE;
            }
            if (realVerifyCode == null) {
                return VERIFY_CODE_EXPIRE_MESSAGE;
            } else if (verifyCode.equals(realVerifyCode)) {
                password = passwordEncoder.encode(password);  // 对密码进行编码
                if (accountMapper.registerAccount(username, password, email) > 0) {
                    return null;  // 成功验证，可进行注册
                } else {
                    return EXCEPTION_MESSAGE;
                }
            } else {
                return WRONG_VERIFY_CODE_MESSAGE;
            }
        } else {
            return "请先请求邮件验证码";
        }
    }

    @Override
    public String validateEmail(String email, String verifyCode, String ipAddress) {
        String key = "email:" + ipAddress + ":" + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            String realVerifyCode = stringRedisTemplate.opsForValue().get(key);  // 获取实际发送的验证码
            if (verifyCode.length() != 6) {
                return WRONG_VERIFY_CODE_MESSAGE;
            }
            if (realVerifyCode == null) {
                return VERIFY_CODE_EXPIRE_MESSAGE;
            } else if (verifyCode.equals(realVerifyCode)) {
                return null;  // 验证码验证成功
            } else {
                return WRONG_VERIFY_CODE_MESSAGE;
            }
        } else {
            return "请先请求邮件验证码";
        }
    }

    @Override
    public String resetPassword(String email, String newPassword) {
        Account account = accountMapper.findAccountByNameOrEmails(email);
        assert account != null: "发生错误，请联系管理员";  // 经过前面步骤，邮件地址应当存在

        if (passwordEncoder.matches(newPassword, account.getPassword())) {
            return "请输入新的密码";
        }

        newPassword = passwordEncoder.encode(newPassword);
        if (accountMapper.updateAccountPasswordByEmail(email, newPassword) > 0) {
            return null;
        } else {
            return EXCEPTION_MESSAGE;
        }
    }

}
