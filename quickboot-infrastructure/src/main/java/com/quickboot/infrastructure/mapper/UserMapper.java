package com.quickboot.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickboot.infrastructure.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口。
 *
 * <p>继承 MyBatis-Plus 的 {@link BaseMapper}，自动获得对 {@link UserEntity} 的
 * 单表 CRUD 能力，无需手写 SQL。可通过 {@code @MapperScan} 或 {@code @Mapper} 注解注册。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
