package com.jackson.controller;

import com.jackson.entity.Result;
import com.jackson.service.ArticleReportService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/my-blog")
public class ArticleReportController {

    @Resource
    private ArticleReportService articleReportService;

    /**
     * 实现文章举报功能
     * @param articleId
     * @return
     */
    @PostMapping("/article/report/{articleId}")
    public Result doArticleReport(@PathVariable Long articleId) {
        return articleReportService.doArticleReport(articleId);
    }
}
