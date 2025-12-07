package com.sky.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.context.BaseContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createUser", BaseContext.getCurrentId(), metaObject);
        this.setFieldValByName("updateUser", BaseContext.getCurrentId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateUser", BaseContext.getCurrentId(), metaObject);
    }
}
