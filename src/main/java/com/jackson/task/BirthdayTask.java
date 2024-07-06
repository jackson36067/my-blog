package com.jackson.task;

import cn.hutool.json.JSONUtil;
import com.jackson.constant.RedisConstant;
import com.jackson.dto.UserInfo;
import com.jackson.mail.MailManagement;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class BirthdayTask {
    @Resource
    private MailManagement mailManagement;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    // 每天8点查看是否有用户的生日到了
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendMailWhenBirthday() {
        String userInfoKey = RedisConstant.USERINFO_KEY;
        // 从缓存中获取所有json格式的UserInfo数据
        Set<String> userInfoJsonStrList = stringRedisTemplate.opsForZSet().range(userInfoKey, 0, -1);
        // 没有用户数据,直接返回
        if (userInfoJsonStrList == null) {
            return;
        }
        // 将数据转换成UserInfo对象并且判断生日时间
        userInfoJsonStrList.stream()
                .map(userinfoStr -> JSONUtil.toBean(userinfoStr, UserInfo.class))
                .toList()
                .forEach(userInfo -> {
                    // 拿现在的日期与用户生日日期对比
                    // 将时间转换为MM-dd形式进行比较
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd");
                    // 将该用户生日的MM-dd与今天的日期MM-dd做对比
                    if (LocalDate.now().format(dateTimeFormatter).equals(userInfo.getBirthday().format(dateTimeFormatter))) {
                        mailManagement.sendBirthdayCode(userInfo.getEmail());
                    }
                });
    }
}
