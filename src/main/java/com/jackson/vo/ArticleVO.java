package com.jackson.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleVO {
    private Long id;
    private String title;
    private String text;
    private String avatar;
    private String username;
    private LocalDateTime createTime;
    private String images;
}
