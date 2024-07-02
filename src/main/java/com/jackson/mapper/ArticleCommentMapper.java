package com.jackson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jackson.entity.ArticleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
}
