package com.jackson.service;

import com.jackson.entity.Result;

public interface FollowService {
    Result doFollow(Long followUserId);

    Result getCommonFollow(Long userId);

    Result getFollowArticle();

    Result getFollowUserList();
}
