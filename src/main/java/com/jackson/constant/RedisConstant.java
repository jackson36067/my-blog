package com.jackson.constant;

public class RedisConstant {
    public static final String USERINFO_KEY = "user:info:";
    public static final String LOGIN_TOKEN_PREFIX = "login:token:";
    public static final Long TOKEN_EXPIRE_TIME = 36000L;
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long CODE_EXPIRE_TIME = 2L;
    public static final String ARTICLE_LIKE_PREFIX = "article:like:";
    public static final String FOLLOW_KEY_PREFIX = "user:follow:";
    public static final String BLOCK_KEY_PREFIX = "user:block:"; // 用户拉黑缓存key前缀
    public static final String REPORT_KEY_PREFIX = "user:report:"; // 用户举报缓存key前缀
}
