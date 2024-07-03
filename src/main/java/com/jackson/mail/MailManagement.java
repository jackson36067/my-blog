package com.jackson.mail;

import com.jackson.constant.EmailConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailManagement {
    @Resource
    private JavaMailSender mailSender;

    /**
     * 发送验证码
     *
     * @param to
     * @return
     */
    public void sendCode(String to, String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(EmailConstant.EMAIL_FROM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(EmailConstant.EMAIL_CODE_SUBJECT);
        simpleMailMessage.setText(code);
        mailSender.send(simpleMailMessage);
    }

    // TODO 对用户生日进行判断, 然后在他们生日当天8点发送邮件表示祝贺

    public void sendBirthdayCode(String to) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(EmailConstant.EMAIL_FROM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(EmailConstant.EMAIL_BIRTHDAY_SUBJECT);
        simpleMailMessage.setText(EmailConstant.HAPPY_BIRTHDAY);
        mailSender.send(simpleMailMessage);
    }
}
