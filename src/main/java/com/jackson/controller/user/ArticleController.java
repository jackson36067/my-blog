package com.jackson.controller.user;

import com.jackson.dto.ArticleDTO;
import com.jackson.entity.Result;
import com.jackson.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/my-blog/article/")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 新增文章
     *
     * @param articleDTO
     * @return
     */
    @PostMapping("/add")
    public Result addArticle(@RequestBody ArticleDTO articleDTO) {
        return articleService.addArticle(articleDTO);
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteArticle(@PathVariable Long id) {
        return articleService.deleteArticle(id);
    }

    /**
     * 修改文章
     *
     * @param articleDTO
     * @return
     */
    @PutMapping("/update")
    public Result updateArticle(@RequestBody ArticleDTO articleDTO) {
        return articleService.updateArticle(articleDTO);
    }

    /**
     * 首页文章展示
     *
     * @param page
     * @param pageSize
     * @param title
     * @return
     */
    @GetMapping("/get")
    public Result getArticleList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam("title") String title
    ) {
        return articleService.getArticleWithPaging(page, pageSize, title);
    }

    /**
     * 查看文章作者主页信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/getMain/{userId}")
    public Result getArticleListByUserId(@PathVariable Long userId) {
        return articleService.getArticleListByUserId(userId);
    }

    /**
     * 文章点赞
     *
     * @param id
     * @return
     */
    @PostMapping("/like/{id}")
    public Result articleLikes(@PathVariable Long id) {
        return articleService.articleLikes(id);
    }

    /**
     * 展示文章详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail/{id}")
    public Result getArticleDetailById(@PathVariable Long id) {
        return articleService.getArticleDetailById(id);
    }

    /**
     * 展示用户点赞列表
     *
     * @return
     */
    @GetMapping("/show/like")
    public Result showLikes() {
        return articleService.showLikes();
    }

    /**
     * 获取自己发布的文章列表
     * @return
     */
    @GetMapping("/show/own")
    public Result showOwnArticles() {
        return articleService.showOwnArticles();
    }
}
