package com.jackson.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.constant.BaseConstant;
import com.jackson.constant.RedisConstant;
import com.jackson.dto.UserInfo;
import com.jackson.dto.UserRegisterDTO;
import com.jackson.exception.CodeErrorException;
import com.jackson.exception.PasswordErrorException;
import com.jackson.exception.UserExistException;
import com.jackson.exception.UserNotFoundException;
import com.jackson.mail.MailManagement;
import com.jackson.entity.User;
import com.jackson.dto.UserCodeDTO;
import com.jackson.dto.UserPasswordDTO;
import com.jackson.mapper.UserMapper;
import com.jackson.entity.Result;
import com.jackson.service.UserService;
import com.jackson.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MailManagement mailManagement;

    public String genToken(User user) {
        String token = UUID.randomUUID(true).toString();
        String tokenKey = RedisConstant.LOGIN_TOKEN_PREFIX + token;
        UserInfo userInfo = BeanUtil.copyProperties(user, UserInfo.class);
        Map<String, Object> userInfoMap = BeanUtil.beanToMap(userInfo, new HashMap<>(), CopyOptions.create()
                .setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) -> (fieldValue.toString())) // 让hash中的存储的值的类型为string
        );
        stringRedisTemplate.opsForHash().putAll(tokenKey, userInfoMap);
        stringRedisTemplate.expire(tokenKey, RedisConstant.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
        log.info("登录成功");
        return token;
    }


    @Override
    public Result loginByPassword(UserPasswordDTO userPasswordDTO) {
        String userName = userPasswordDTO.getUsername();
        User user = this.query().eq("username", userName).one();
        if (user == null) {
            throw new UserNotFoundException(BaseConstant.USER_NOT_FOUND);
        }
        if (!userPasswordDTO.getPassword().equals(user.getPassword())) {
            throw new PasswordErrorException(BaseConstant.PASSWORD_ERROR);
        }
        String token = genToken(user);
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .token(token)
                .userName(user.getUsername())
                .build();
        return Result.success(userVO);
    }

    @Override
    public Result loginByCode(UserCodeDTO userCodeDTO) {
        User user = this.query().eq("email", userCodeDTO.getEmail()).one();
        if (user == null) {
            throw new UserNotFoundException("请先注册");
        }
        if (!userCodeDTO.getCode().equals(stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_CODE_KEY + userCodeDTO.getEmail()))) {
            throw new CodeErrorException(BaseConstant.CODE_ERROR);
        }
        String token = genToken(user);
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .token(token)
                .userName(user.getUsername())
                .build();
        return Result.success(userVO);
    }

    @Override
    public Result sendCode(String email) {
        sendCodeAndCacheCode(email);
        log.info("发送验证码成功");
        return Result.success();
    }

    private void sendCodeAndCacheCode(String email) {
        String code = RandomUtil.randomNumbers(6);
        mailManagement.sendCode(email, code);
        String codeKey = RedisConstant.LOGIN_CODE_KEY + email;
        stringRedisTemplate.opsForValue()
                .set(codeKey, code, RedisConstant.CODE_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    @Override
    public Result register(UserRegisterDTO userRegisterDTO) {
        String email = userRegisterDTO.getEmail();
        User user = this.query().eq("email", email).one();
        if (user != null) {
            throw new UserExistException(BaseConstant.USER_EXIST);
        }
        sendCodeAndCacheCode(email);
        user = User.builder()
                .username(userRegisterDTO.getUserName())
                .avatar(null)
                .email(email)
                .password(userRegisterDTO.getPassword())
                .birthday(userRegisterDTO.getBirthday())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        this.save(user);
        return Result.success();
    }
}
