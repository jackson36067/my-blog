package com.jackson.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.jackson.constant.RedisConstant;
import com.jackson.dto.UserInfo;
import com.jackson.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("authorization");
        if (StrUtil.isEmpty(authorization)) {
            return true;
        }
        String tokenKey = RedisConstant.LOGIN_TOKEN_PREFIX + authorization;
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(tokenKey);
        if (userMap.isEmpty()) {
            return true;
        }
        UserInfo userInfo = BeanUtil.fillBeanWithMap(userMap, new UserInfo(), false);
        UserHolder.setUser(userInfo);
        // 刷新token过期时间
        redisTemplate.expire(tokenKey, RedisConstant.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}

