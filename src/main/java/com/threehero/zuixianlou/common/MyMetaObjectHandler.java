package com.threehero.zuixianlou.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

  /**
   * 插入操作自动填充
   * @param metaObject
   */
  @Override
  public void insertFill(MetaObject metaObject) {
    log.info("公共字段自动填充[insert]...");
    log.info(metaObject.toString());
    metaObject.setValue("createTime", LocalDateTime.now());
    metaObject.setValue("updateTime", LocalDateTime.now());
    metaObject.setValue("createUser", BaseContext.getCurrentId());
    metaObject.setValue("updateUser", BaseContext.getCurrentId());
  }

  /**
   * 更新操作自动填充
   * @param metaObject
   */
  @Override
  public void updateFill(MetaObject metaObject) {
    log.info("公共字段自动填充[update]...");
    log.info(metaObject.toString());
    metaObject.setValue("updateTime", LocalDateTime.now());
    long id = Thread.currentThread().getId();
    log.info("id: {}", id);
    metaObject.setValue("updateUser", BaseContext.getCurrentId());

  }
}
