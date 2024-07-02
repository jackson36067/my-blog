package com.jackson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jackson.entity.Article;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {
    List<Article> getArticlesWithPaging(Integer start, Integer pageSize, String title,List<Long> userBlockIdList);
}
