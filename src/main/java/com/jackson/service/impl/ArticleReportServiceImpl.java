package com.jackson.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.constant.RedisConstant;
import com.jackson.entity.ArticleReport;
import com.jackson.entity.Result;
import com.jackson.exception.ReportAgainException;
import com.jackson.mapper.ArticleReportMapper;
import com.jackson.service.ArticleReportService;
import com.jackson.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleReportServiceImpl extends ServiceImpl<ArticleReportMapper, ArticleReport> implements ArticleReportService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 实现文章举报功能
     *
     * @param articleId
     * @return
     */
    @Override
    public Result doArticleReport(Long articleId) {
        // 1.判断该用户是否举报过该文章
        // 1.1 获取缓存中的数据,判断是否存在
        Long userId = UserHolder.getUser().getId();
        // 以举报者id作为key, 文章的id作为value
        String userReportKey = RedisConstant.REPORT_KEY_PREFIX + userId;
        Double score = stringRedisTemplate.opsForZSet().score(userReportKey, articleId);
        // 1.1.1 已存在 -> 抛出异常
        if (score == null) {
            throw new ReportAgainException("请勿重复举报,我们已在核实中");
        }
        // 1.1.2 不存在 -> 保存数据
        // 1.1.2.1 将举报数据加入到数据库中
        this.save(new ArticleReport(userId, articleId));
        // 1.1.2.2 将举报数据缓存到redis中
        stringRedisTemplate.opsForZSet().add(userReportKey, articleId.toString(), System.currentTimeMillis());
        return Result.success();
    }
}
