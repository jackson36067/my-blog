package com.jackson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "tb_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String email;
    private String username;
    private String password;
    private String avatar;
    private LocalDate birthday;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
