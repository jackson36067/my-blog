package com.jackson.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jackson.utils.EmployeeHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// 实现公共字段自动填充
@Component
public class AutoFillHandler implements MetaObjectHandler {

    /**
     * 当实体类Employee进行了新增操作时会自动执行该方法
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "create_time", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "update_time", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "create_user", Long.class, EmployeeHolder.getEmployeeInfo().getId());
        this.strictInsertFill(metaObject, "update_user", Long.class, EmployeeHolder.getEmployeeInfo().getId());
    }

    /**
     * 当实体类Employee进行了更新操作时,会自动执行该方法
     * @param metaObject 元对象
     */

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "update_time", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "update_user", Long.class, EmployeeHolder.getEmployeeInfo().getId());
    }
}
