package com.jackson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "tb_article")
public class Article implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String title;
    private String text;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String image;
    private Integer comments; // 评论数
    private Integer likes; // 点赞数
    @TableField(exist = false)
    private boolean isLiked;
}
