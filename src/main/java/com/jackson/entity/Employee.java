package com.jackson.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tb_employee")
public class Employee {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String avatar;
    private String phone;
    private String sex;
    private String idNumber;
    private String status; // 账号状态
    private String authority; // 权限等级
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
