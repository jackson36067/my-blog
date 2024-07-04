package com.jackson.controller.user;

import com.jackson.entity.Result;
import com.jackson.service.ArticleCommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/my-blog")
public class ArticleCommentController {

    @Resource
    private ArticleCommentService articleCommentService;

    @PostMapping("/send/comment")
    public Result sendComment(@RequestParam("id") Long articleId, @RequestParam("comment") String comment) {
        return articleCommentService.sendComment(articleId,comment);
    }
}
