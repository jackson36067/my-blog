package com.jackson.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.constant.BaseConstant;
import com.jackson.constant.RedisConstant;
import com.jackson.dto.ArticleDTO;
import com.jackson.dto.UserInfo;
import com.jackson.entity.*;
import com.jackson.exception.ArticleNotExistException;
import com.jackson.mapper.ArticleMapper;
import com.jackson.service.ArticleService;
import com.jackson.utils.UserHolder;
import com.jackson.vo.ArticleVO;
import jodd.util.StringUtil;
import com.jackson.vo.ArticlePageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserServiceImpl userService;
    @Resource
    private ArticleCommentServiceImpl articleCommentService;
    @Resource
    private ArticleLikeServiceImpl articleLikeService;
    @Resource
    private UserBlockServiceImpl userBlockService;

    /**
     * 新增文章
     *
     * @param articleDTO
     * @return
     */
    @Override
    public Result addArticle(ArticleDTO articleDTO) {
        Article article = BeanUtil.copyProperties(articleDTO, Article.class);
        UserInfo userInfo = UserHolder.getUser();
        Long userId = userInfo.getId();
        article.setUserId(userId);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        this.save(article);
        log.info("发布文章成功");
        return Result.success();
    }

    /**
     * 根据id删除文章
     *
     * @param articleId
     * @return
     */
    @Override
    public Result deleteArticle(Long articleId) {
        // 从tb_article数据库中删除该文章
        this.deleteArticle(articleId);
        // 将缓存中缓存的对文章点赞的数据进行清除
        String likesKey = RedisConstant.ARTICLE_LIKE_PREFIX + articleId;
        stringRedisTemplate.opsForZSet().removeRange(likesKey, 0, -1);
        // 删除tb_article_comment中所有文章的评论数据
        QueryWrapper<ArticleComment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("article_id", articleId);
        articleCommentService.remove(commentWrapper);
        // 删除tb_article_like中所有文章点赞的数据
        QueryWrapper<ArticleLike> likeWrapper = new QueryWrapper<>();
        likeWrapper.eq("article_id", articleId);
        articleLikeService.remove(likeWrapper);
        log.info("删除文章:{}", articleId);
        return Result.success();
    }

    /**
     * 修改文章
     *
     * @param articleDTO
     * @return
     */
    @Override
    public Result updateArticle(ArticleDTO articleDTO) {
        Article article = BeanUtil.copyProperties(articleDTO, Article.class);
        this.updateById(article);
        return Result.success();
    }


    /**
     * 首页分页展示文章
     *
     * @param page
     * @param pageSize
     * @param title
     * @return
     */
    @Override
    public Result getArticleWithPaging(Integer page, Integer pageSize, String title) {
        int start = (page - 1) * pageSize;
        Long userId = UserHolder.getUser().getId();
        // 将拉黑的用户的文章舍去
        Set<String> userBlockIdStrSet = stringRedisTemplate.opsForZSet().range(RedisConstant.BLOCK_KEY_PREFIX + userId, 0, -1);
        if (userBlockIdStrSet == null) {
            return Result.success(List.of());
        }
        List<Long> userBlockIdList = userBlockIdStrSet.stream().map(Long::valueOf).toList();
        List<Article> articlesList = articleMapper.getArticlesWithPaging(start, pageSize, title, userBlockIdList);
        List<ArticlePageVO> articlePageVOList = getArticlePageVOS(articlesList, false);
        return Result.success(articlePageVOList);
    }

    /**
     * 用户封装首页以及个人主页文章内容对象
     *
     * @param articlesList 文章对象集合
     * @return
     */
    private List<ArticlePageVO> getArticlePageVOS(List<Article> articlesList, boolean isBlock) {
        // 返回一个ArticlePageVo对象集合
        return articlesList.stream().map((Article a) -> {
            User user = userService.query().eq("id", a.getUserId()).one();
            // 获取前五个点赞的人
            Set<String> LikedUserIdStrList = stringRedisTemplate.opsForZSet().range(RedisConstant.ARTICLE_LIKE_PREFIX + a.getId(), 0, 4);
            List<Long> LikeUserIdList = List.of();
            if (LikedUserIdStrList != null) {
                LikeUserIdList = LikedUserIdStrList.stream().map(Long::valueOf).toList();
            }
            String idStr = StringUtil.join(LikedUserIdStrList, ",");
            List<User> userList = userService.query().in("id", LikeUserIdList).last("order by field (id" + idStr + ")").list();
            return ArticlePageVO.builder()
                    .id(a.getId())
                    .avatar(user.getAvatar())
                    .username(user.getUsername())
                    .image(a.getImage())
                    .createTime(a.getCreateTime())
                    .text(a.getText())
                    .title(a.getTitle())
                    .likes(a.getLikes())
                    .isLiked(isLiked(a.getId()))
                    .likedUserList(userList)
                    .comments(a.getComments())
                    .userComment(null)
                    .authorComment(null)
                    .isFollow(null)
                    .isBlock(isBlock)
                    .build();
        }).toList();
    }

    /**
     * 用户主页信息展示
     *
     * @param userId
     * @return
     */
    @Override
    public Result getArticleListByUserId(Long userId) {
        List<Article> articleList = this.query().eq("user_id", userId).list();
        // 判断是否拉黑该用户
        boolean isBlock = isBlock(userId);
        List<ArticlePageVO> articlePageVOList = getArticlePageVOS(articleList, isBlock);
        return Result.success(articlePageVOList);
    }

    /**
     * 文章点赞
     *
     * @param id 文章id
     * @return
     */
    @Override
    public Result articleLikes(Long id) {
        Article article = this.query().eq("id", id).one();
        if (article == null) {
            throw new ArticleNotExistException(BaseConstant.ARTICLE_NOT_FOUND);
        }
        // 以文章id存为key值,value值保存点赞用户的id
        String likeKey = RedisConstant.ARTICLE_LIKE_PREFIX + id;
        Double score = stringRedisTemplate.opsForZSet().score(likeKey, id);
        Long userId = UserHolder.getUser().getId();
        if (score == null) {
            this.update().setSql("likes = likes + 1").eq("id", id).update();
            articleLikeService.save(new ArticleLike(userId, id));
            stringRedisTemplate.opsForZSet().add(likeKey, userId.toString(), System.currentTimeMillis());
        } else {
            this.update().setSql("likes = likes -1 ").eq("id", id).update();
            QueryWrapper<ArticleLike> wrapper = new QueryWrapper<>();
            // 封装删除条件
            wrapper.eq("user_id", userId)
                    .eq("article_id", id);
            articleLikeService.remove(wrapper);
            stringRedisTemplate.opsForZSet().remove(likeKey, userId);
        }
        return Result.success();
    }

    /**
     * 展示文章详细
     *
     * @param id 文章id
     * @return
     */
    @Override
    public Result getArticleDetailById(Long id) {
        // 根据文章id获取文章对象
        Article article = this.query().eq("id", id).one();
        // 为空直接报错
        if (article == null) {
            throw new ArticleNotExistException(BaseConstant.ARTICLE_NOT_FOUND);
        }
        // 根据文章发布的用户id获取用户信息
        User user = userService.query().eq("id", article.getUserId()).one();
        // 获取redis缓存的文章点赞数据
        String likeKey = RedisConstant.ARTICLE_LIKE_PREFIX + id;
        Set<String> likesIdStrList = stringRedisTemplate.opsForZSet().range(likeKey, 0, -1);
        // 如果没人点赞默认返回空集合
        List<User> likesUserList = List.of();
        if (likesIdStrList != null) {
            // 不为空,获取到点赞的用户id集合
            List<Long> likesIdList = likesIdStrList.stream().map(Long::valueOf).toList();
            // 获取所有点赞的用户对象集合
            likesUserList = userService.query().in("id", likesIdList).list();
        }
        // 获取作者发送的评论
        List<ArticleComment> articleCommentList = articleCommentService.query().eq("article_id", id).orderByDesc("create_time").list();
        List<String> authorCommentList = articleCommentList.stream().filter(articleComment -> Objects.equals(articleComment.getUserId(), user.getId())).map(ArticleComment::getComment).toList();
        // 获取其他用户发送的评论
        List<String> userCommentList = articleCommentList.stream().filter(articleComment -> !Objects.equals(articleComment.getUserId(), user.getId())).map(ArticleComment::getComment).toList();
        // 封装对象返回结果
        ArticlePageVO articlePageVO = ArticlePageVO.builder()
                .id(id)
                .image(article.getImage())
                .avatar(user.getAvatar())
                .username(user.getUsername())
                .createTime(article.getCreateTime())
                .text(article.getText())
                .title(article.getTitle())
                .isLiked(isLiked(id))
                .likes(article.getLikes())
                .likedUserList(likesUserList)
                .comments(article.getComments())
                .userComment(userCommentList)
                .authorComment(authorCommentList)
                .isFollow(isFollow(article.getUserId()))
                .build();
        return Result.success(articlePageVO);
    }

    /**
     * 展示用户点赞列表
     *
     * @return
     */
    @Override
    public Result showLikes() {
        List<ArticleLike> articleLikeList = articleLikeService.query().eq("user_id", UserHolder.getUser().getId()).list();
        List<ArticlePageVO> articlePageVOS = articleLikeList.stream().map((ArticleLike articleLike) -> {
            Long articleId = articleLike.getArticleId();
            Article article = this.query().eq("id", articleId).one();
            User user = userService.query().eq("id", article.getUserId()).one();
            String articleLikeKey = RedisConstant.ARTICLE_LIKE_PREFIX + articleId;
            Set<String> likeUserIdList = stringRedisTemplate.opsForZSet().range(articleLikeKey, 0, 4);
            String userIdStr = StringUtil.join(likeUserIdList, ",");
            List<User> likeUserList = userService.query()
                    .in("id", likeUserIdList)
                    .last("order by field (id" + userIdStr + ")").list();
            return ArticlePageVO.builder()
                    .id(articleId)
                    .username(user.getUsername())
                    .avatar(user.getAvatar())
                    .image(article.getImage())
                    .createTime(article.getCreateTime())
                    .title(article.getTitle())
                    .text(article.getText())
                    .likes(article.getLikes())
                    .comments(article.getComments())
                    .likedUserList(likeUserList)
                    .build();
        }).toList();
        return Result.success(articlePageVOS);
    }

    /**
     * 判断是否点赞
     *
     * @return
     */
    private boolean isLiked(Long articleId) {
        UserInfo user = UserHolder.getUser();
        if (user == null) {
            return false;
        }
        Double score = stringRedisTemplate.opsForZSet().score(RedisConstant.ARTICLE_LIKE_PREFIX + articleId, user.getId());
        return BooleanUtil.isTrue(score != null);
    }

    /**
     * 判断是否关注
     *
     * @param articleUserId
     * @return
     */
    private boolean isFollow(Long articleUserId) {
        String followKey = RedisConstant.FOLLOW_KEY_PREFIX + articleUserId;
        Double score = stringRedisTemplate.opsForZSet().score(followKey, UserHolder.getUser().getId());
        return BooleanUtil.isTrue(score != null);
    }

    /**
     * 是否拉黑该用户
     *
     * @param userBlockId
     * @return
     */
    private boolean isBlock(Long userBlockId) {
        Long userId = UserHolder.getUser().getId();
        UserBlock userBlock = userBlockService.query().eq("user_id", userId).eq("user_block_id", userBlockId).one();
        return BooleanUtil.isTrue(userBlock != null);
    }
}
