package com.jackson.controller;

import com.jackson.entity.Result;
import com.jackson.service.UserBlockService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/my-blog")
public class UserBlockController {
    @Resource
    private UserBlockService userBlockService;

    /**
     * 拉黑用户
     * @param userBlockId
     * @return
     */
    @PostMapping("/block/{userBlockId}")
    public Result doBlock(@PathVariable Long userBlockId) {
        return userBlockService.doBlock(userBlockId);
    }

    /**
     * 取消拉黑
     * @param userBlockId
     * @return
     */
    @PostMapping("/unblock/{userBlockId}")
    public Result unBlock(@PathVariable Long userBlockId){
        return userBlockService.unBlock(userBlockId);
    }

    /**
     * 获取该用户拉黑的用户列表
     * @return
     */
    @GetMapping("/get/block")
    public Result getBlockList(){
        return userBlockService.getBlockList();
    }
}
