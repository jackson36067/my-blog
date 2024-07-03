package com.jackson.controller;

import com.jackson.dto.UserCodeDTO;
import com.jackson.dto.UserInfo;
import com.jackson.dto.UserPasswordDTO;
import com.jackson.dto.UserRegisterDTO;
import com.jackson.entity.Result;
import com.jackson.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-blog")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 使用密码登录
     *
     * @param userPasswordDTO
     * @return
     */
    @PostMapping("/login/password")
    public Result loginByPassword(@RequestBody UserPasswordDTO userPasswordDTO) {
        return userService.loginByPassword(userPasswordDTO);
    }

    /**
     * 使用验证码登录/注册
     *
     * @param userCodeDTO
     * @return
     */
    @PostMapping("/login/code")
    public Result loginByCode(@RequestBody UserCodeDTO userCodeDTO) {
        return userService.loginByCode(userCodeDTO);
    }

    @PostMapping("/code")
    public Result sendCode(String email) {
        return userService.sendCode(email);
    }

    @PostMapping
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    /**
     * 用户修改个人信息功能实现
     * @param userInfo
     * @return
     */
    @PostMapping("/update/userInfo")
    public Result updateUserInfo(@RequestBody UserInfo userInfo) {
        return userService.updateUserInfo(userInfo);
    }
}
