package com.jackson.vo;

import com.jackson.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticlePageVO implements Serializable {
    private Long id;
    private String title;
    private String text;
    private String avatar;
    private String username;
    private LocalDateTime createTime;
    private String image;
    private Integer likes;
    private List<User> likedUserList;
    private Boolean isLiked;
    private Integer comments;
    private List<String> userComment;
    private List<String> authorComment;
    private Boolean isFollow;
    private Boolean isBlock; // 是否拉黑
}
