package com.jackson.controller.user;

import com.jackson.entity.Result;
import com.jackson.service.FollowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/my-blog")
public class FollowController {
    @Resource
    private FollowService followService;

    /**
     * 关注
     *
     * @param followUserId
     * @return
     */
    @PostMapping("/follow/{followUserId}")
    public Result doFollow(@PathVariable Long followUserId) {
        return followService.doFollow(followUserId);
    }

    /**
     * 查看共同关注
     *
     * @param userId
     * @return
     */
    @GetMapping("/common/follow/{userId}")
    public Result getCommonFollow(@PathVariable Long userId) {
        return followService.getCommonFollow(userId);
    }

    /**
     * 查看关注者新发布的文章
     * @return
     */
    @GetMapping("/follow/article")
    public Result getFollowArticle() {
        return followService.getFollowArticle();
    }

    /**
     * 获取关注列表
     * @return
     */
    @GetMapping("/show/follow")
    public Result getFollowUserList(){
        return followService.getFollowUserList();
    }
}
