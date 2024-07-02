package com.jackson.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.entity.ArticleComment;
import com.jackson.entity.Result;
import com.jackson.mapper.ArticleCommentMapper;
import com.jackson.service.ArticleCommentService;
import com.jackson.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment> implements ArticleCommentService {
    /**
     * 发布评论
     * @param articleId
     * @param comment
     * @return
     */
    @Override
    public Result sendComment(Long articleId, String comment) {
        ArticleComment articleComment = ArticleComment.builder()
                .userId(UserHolder.getUser().getId())
                .articleId(articleId)
                .comment(comment)
                .build();
        this.save(articleComment);
        log.info("发送评论成功");
        return Result.success();
    }
}
