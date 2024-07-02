package com.jackson.task;

import com.jackson.websocket.WebSocketServer;
import com.jackson.constant.RedisConstant;
import com.jackson.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class ArticleTask {

    @Resource
    private WebSocketServer webSocketServer;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 每分钟检查收件箱,返回收件箱文章个数
     */
    @Scheduled(cron = "0 * * * * ?")
    public void pushArticleAmount() {
        String followInboxKey = RedisConstant.FOLLOW_INBOX_PREFIX + UserHolder.getUser().getId();
        Set<String> followArticleSet = stringRedisTemplate.opsForZSet().range(followInboxKey, 0, -1);
        if (followArticleSet != null) {
            // 返回收到的文章的数量
            webSocketServer.sendToAllClient(String.valueOf(followArticleSet.size()));
        }
    }
}
