package com.jackson.utils;

import com.jackson.dto.UserInfo;
import com.jackson.dto.UserPasswordDTO;

public class UserHolder {
    public static java.lang.ThreadLocal<UserInfo> threadLocal = new java.lang.ThreadLocal<>();

    public static void setUser(UserInfo userInfo) {
        threadLocal.set(userInfo);
    }

    public static UserInfo getUser() {
        return threadLocal.get();
    }

    public static void removeUser(){
        threadLocal.remove();
    }
}
