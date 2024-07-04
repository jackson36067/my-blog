package com.jackson.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.constant.RedisConstant;
import com.jackson.dto.UserInfo;
import com.jackson.entity.Article;
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

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserServiceImpl userService;
    @Resource
    private ArticleServiceImpl articleService;

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
        // 将关注数据缓存到redis中, key为该用户 value存储该用户关注的用户id
        String followKey = RedisConstant.FOLLOW_KEY_PREFIX + userId;
        stringRedisTemplate.opsForZSet().add(followKey, followUserId.toString(), System.currentTimeMillis());
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
        Long iUserId = UserHolder.getUser().getId();
        // 获取二者关注的用户的id
        Set<String> iFollowUserIdStrSet = stringRedisTemplate.opsForZSet().range(RedisConstant.FOLLOW_KEY_PREFIX + iUserId, 0, -1);
        Set<String> aFollowUserIdStrSet = stringRedisTemplate.opsForZSet().range(RedisConstant.FOLLOW_KEY_PREFIX + userId, 0, -1);
        List<UserInfo> list = List.of();
        if (iFollowUserIdStrSet == null || aFollowUserIdStrSet == null) {
            return Result.success(list);
        }
        List<Long> anotherFollowIdList = aFollowUserIdStrSet.stream().map(Long::valueOf).toList();
        // 过滤出共同关注的用户的id
        List<Long> commonFollowUserId = iFollowUserIdStrSet
                .stream()
                .map(Long::valueOf)
                .toList()
                .stream()
                .filter(
                        followUserId -> anotherFollowIdList.stream().anyMatch(anotherFollowUserId -> Objects.equals(followUserId, anotherFollowUserId))
                ).toList();
        list = userService.query().in("id", commonFollowUserId)
                .list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserInfo.class))
                .toList();
        return Result.success(list);
    }

    /**
     * 个人主页->根据发布时间展示关注者发布的文章
     *
     * @return
     */
    @Override
    public Result getFollowArticle() {
        Long userId = UserHolder.getUser().getId();
        String userFollowKey = RedisConstant.FOLLOW_KEY_PREFIX + userId;
        // 获取所有被当前用户关注的用户的id
        Set<String> userFollowIdStrSet = stringRedisTemplate.opsForZSet().range(userFollowKey, 0, -1);
        // 没有关注的人 -> 返回空数据
        if (userFollowIdStrSet == null) {
            return Result.success(List.of());
        }
        List<Long> userFollowIdList = userFollowIdStrSet.stream()
                .map(Long::valueOf)
                .toList();
        // 封装ArticleVO对象返回
        List<Article> articleList = articleService.query().in("user_id", userFollowIdList).orderBy(true, false, "create_time").list();
        List<ArticleVO> articleVOS = articleList.stream()
                .map((Article article) -> {
                    User user = userService.query().eq("id", article.getUserId()).one();
                    ArticleVO articleVO = BeanUtil.copyProperties(article, ArticleVO.class);
                    articleVO.setUsername(user.getUsername());
                    articleVO.setAvatar(user.getAvatar());
                    return articleVO;
                })
                .toList();
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
        Set<String> followUserIdStrSet = stringRedisTemplate.opsForZSet().reverseRange(RedisConstant.FOLLOW_KEY_PREFIX + userId, 0, -1);
        if (followUserIdStrSet == null) {
            return Result.success(List.of());
        }
        List<Long> followUserIdList = followUserIdStrSet.stream().map(Long::valueOf).toList();
        List<UserInfo> userInfoList = followUserIdList.stream().map(followUserId -> {
            User user = userService.query().eq("id", followUserId).one();
            return BeanUtil.copyProperties(user, UserInfo.class);
        }).toList();
        return Result.success(userInfoList);
    }
}