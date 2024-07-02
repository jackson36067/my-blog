package com.jackson.service;

import com.jackson.entity.Result;

public interface ArticleCommentService {
    Result sendComment(Long articleId, String comment);
}
