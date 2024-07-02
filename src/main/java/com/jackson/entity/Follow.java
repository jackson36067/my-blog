package com.jackson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tb_user_follow")
public class Follow {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long userFollowId;
}
