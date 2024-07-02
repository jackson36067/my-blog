package com.jackson.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.constant.RedisConstant;
import com.jackson.dto.UserInfo;
import com.jackson.entity.Follow;
import com.jackson.entity.Result;
import com.jackson.entity.User;
import com.jackson.mapper.FollowMapper;
import com.jackson.service.FollowService;
import com.jackson.utils.UserHolder;
import com.jackson.vo.ArticleVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserServiceImpl userService;

    /**
     * 关注用户
     *
     * @param followUserId
     * @return
     */
    @Override
    public Result doFollow(Long followUserId) {
        Long userId = UserHolder.getUser().getId();
        Follow follow = Follow.builder()
                .userId(userId)
                .userFollowId(followUserId)
                .build();
        this.save(follow);
        String followKey = RedisConstant.FOLLOW_KEY_PREFIX + followUserId;
        stringRedisTemplate.opsForZSet().add(followKey, userId.toString(), System.currentTimeMillis());
        log.info("关注成功");
        return Result.success();
    }

    /**
     * 获取共同关注
     *
     * @param userId
     * @return
     */
    @Override
    public Result getCommonFollow(Long userId) {
        List<Follow> userFollows = this.query().eq("user_id", UserHolder.getUser().getId()).list();
        List<Follow> anotherUserFollows = this.query().eq("user_id", userId).list();
        // 过滤出两个集合关注一致的用户
        List<User> commonFollowUser = userFollows.stream()
                .filter(follow -> anotherUserFollows.stream()
                        .anyMatch(anotherFollow -> Objects.equals(anotherFollow.getUserFollowId(), follow.getUserFollowId())))
                .map(follow -> userService.query().eq("id", follow.getUserFollowId()).one())
                .toList();
        return Result.success(commonFollowUser);
    }

    /**
     * 个人主页->获取已关注的人新发送的文章
     *
     * @return
     */
    @Override
    public Result getFollowArticle() {
        String followInboxKey = RedisConstant.FOLLOW_INBOX_PREFIX + UserHolder.getUser().getId();
        Set<String> followArticleVOs = stringRedisTemplate.opsForZSet().range(followInboxKey, 0, -1);
        if (followArticleVOs == null) {
            return Result.success(null);
        }
        String temFollowArticleKey = RedisConstant.TEMPORARY_FOLLOW_INBOX_PREFIX + System.currentTimeMillis();
        // 获取收件箱中的数据
        followArticleVOs.forEach(followArticleVOsStrJson -> {
                    // 将他保存到另一个地方用于展示
                    // key使用时间戳用于区分
                    stringRedisTemplate.opsForZSet().add(temFollowArticleKey, followArticleVOsStrJson, System.currentTimeMillis());
                    // 将过期时间设置为1天
                    stringRedisTemplate.expire(temFollowArticleKey, 1, TimeUnit.DAYS);
                }
        );
        // 将收件箱清空
        stringRedisTemplate.opsForZSet().removeRange(followInboxKey, 0, -1);
        // 从临时收件箱中获取新发的文章进行展示
        Set<String> temFollowArticleJsonStrSet = stringRedisTemplate.opsForZSet().range(temFollowArticleKey, 0, -1);
        List<ArticleVO> articleVOS = new ArrayList<>(List.of());
        temFollowArticleJsonStrSet.forEach(temFollowArticleJsonStr -> {
            articleVOS.add(JSONUtil.toBean(temFollowArticleJsonStr, ArticleVO.class));
        });
        return Result.success(articleVOS);
    }

    /**
     * 获取关注用户列表
     *
     * @return
     */
    @Override
    public Result getFollowUserList() {
        Long userId = UserHolder.getUser().getId();
        List<Follow> followList = this.query().eq("user_id", userId).list();
        List<UserInfo> userInfoList = followList.stream().map((Follow follow) -> {
            User user = userService.query().eq("id", follow.getUserFollowId()).one();
            return BeanUtil.copyProperties(user, UserInfo.class);
        }).toList();
        return Result.success(userInfoList);
    }
}