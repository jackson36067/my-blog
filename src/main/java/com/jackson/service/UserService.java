package com.jackson.service;

import com.jackson.dto.UserCodeDTO;
import com.jackson.dto.UserInfo;
import com.jackson.dto.UserPasswordDTO;
import com.jackson.dto.UserRegisterDTO;
import com.jackson.entity.Result;

public interface UserService {
    Result loginByPassword(UserPasswordDTO userPasswordDTO);

    Result loginByCode(UserCodeDTO userCodeDTO);

    Result sendCode(String email);

    Result register(UserRegisterDTO userRegisterDTO);

    Result updateUserInfo(UserInfo userInfo);
}
