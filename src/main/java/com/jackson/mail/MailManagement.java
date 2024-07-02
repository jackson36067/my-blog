package com.jackson.mail;

import cn.hutool.core.util.RandomUtil;
import com.jackson.constant.EmailConstant;
import com.jackson.constant.RedisConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MailManagement {
    @Resource
    private JavaMailSender mailSender;

    /**
     * 发送验证码
     * @param to
     * @return
     */
    public void sendCode(String to,String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(EmailConstant.EMAIL_FROM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(EmailConstant.EMAIL_SUBJECT);
        simpleMailMessage.setText(code);
        mailSender.send(simpleMailMessage);
    }
}
