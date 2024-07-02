package com.jackson.constant;

public class RedisConstant {
    public static final String LOGIN_TOKEN_PREFIX = "login:token:";
    public static final Long TOKEN_EXPIRE_TIME = 36000L;
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long CODE_EXPIRE_TIME = 2L;
    public static final String ARTICLE_LIKE_PREFIX = "article:like:";
    public static final String FOLLOW_KEY_PREFIX = "user:follow:";
    public static final String FOLLOW_INBOX_PREFIX = "follow:inbox:"; // 粉丝收件箱key前缀
    public static final String TEMPORARY_FOLLOW_INBOX_PREFIX = "temporary:inbox:"; // 当用户查看关注者推送的文章时用它把文章保存起来,然后把收件箱清空
    public static final String BLOCK_KEY_PREFIX = "user:block:"; // 用户拉黑缓存key前缀
}
