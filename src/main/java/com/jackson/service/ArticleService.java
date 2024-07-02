package com.jackson.service;

import com.jackson.dto.ArticleDTO;
import com.jackson.entity.Result;

public interface ArticleService {
    Result addArticle(ArticleDTO articleDTO);

    Result deleteArticle(Long articleId);

    Result updateArticle(ArticleDTO articleDTO);

    Result getArticleWithPaging(Integer page, Integer pageSize, String title);

    Result getArticleListByUserId(Long userId);

    Result articleLikes(Long id);

    Result getArticleDetailById(Long id);

    Result showLikes();
}
