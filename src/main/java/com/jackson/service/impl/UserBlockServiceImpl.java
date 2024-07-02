package com.jackson.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.constant.RedisConstant;
import com.jackson.entity.Follow;
import com.jackson.entity.Result;
import com.jackson.entity.User;
import com.jackson.entity.UserBlock;
import com.jackson.mapper.UserBlockMapper;
import com.jackson.service.UserBlockService;
import com.jackson.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserBlockServiceImpl extends ServiceImpl<UserBlockMapper, UserBlock> implements UserBlockService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FollowServiceImpl followService;
    @Resource
    private UserServiceImpl userService;

    @Override
    public Result doBlock(Long userBlockId) {
        // 向tb_user_block表中插入数据
        Long userId = UserHolder.getUser().getId();
        UserBlock userBlock = UserBlock.builder()
                .userId(userId)
                .userBlockId(userBlockId)
                .build();
        this.save(userBlock);
        // 以用户id作为key缓存, value存储被该用户拉黑的用户数据
        String blockKey = RedisConstant.BLOCK_KEY_PREFIX + userId;
        stringRedisTemplate.opsForZSet().add(blockKey, userBlock.toString(), System.currentTimeMillis());
        // 判断用户是否关注该用户 -> 将用户关注该用户的数据删除
        Follow follow = followService.query().eq("user_id", userId).eq("user_follow_id", userBlock).one();
        if (follow != null) {
            // 将缓存的关注数据删除
            stringRedisTemplate.opsForZSet().remove(RedisConstant.FOLLOW_KEY_PREFIX + userBlock, userId);
            // 将数据库中的关注数据删除
            QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("user_follow_id", userBlock);
            followService.remove(queryWrapper);
        }
        log.info("拉黑用户:{}成功", userBlockId);
        return Result.success();
    }

    /**
     * 取消拉黑
     *
     * @param userBlockId
     * @return
     */
    @Override
    public Result unBlock(Long userBlockId) {
        // 将拉黑数据从数据中移除
        Long userId = UserHolder.getUser().getId();
        QueryWrapper<UserBlock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("user_block_id", userBlockId);
        this.remove(queryWrapper);
        // 将缓存中缓存的拉黑数据删除
        stringRedisTemplate.opsForZSet().remove(RedisConstant.BLOCK_KEY_PREFIX + userId, userBlockId);
        log.info("取消拉黑成功");
        return Result.success();
    }

    /**
     * 获取用户拉黑的用户列表
     *
     * @return
     */
    @Override
    public Result getBlockList() {
        // 从缓存中获取该用户拉黑的所有用户id
        Long userId = UserHolder.getUser().getId();
        String blockKey = RedisConstant.BLOCK_KEY_PREFIX + userId;
        Set<String> userBlockIdStrList = stringRedisTemplate.opsForZSet().range(blockKey, 0, -1);
        // 没有 -> 返回空值
        if (userBlockIdStrList == null) {
            return Result.success(null);
        }
        // 有,返回所有拉黑用户数据
        List<Long> userBlockIdList = userBlockIdStrList.stream().map(Long::valueOf).toList();
        List<User> blockUserList = userService.query().in("id", userBlockIdList).list();
        List<UserHolder> userHolderList = blockUserList.stream()
                .map(blockUser -> BeanUtil.copyProperties(blockUser, UserHolder.class)).toList();
        return Result.success(userHolderList);
    }
}
