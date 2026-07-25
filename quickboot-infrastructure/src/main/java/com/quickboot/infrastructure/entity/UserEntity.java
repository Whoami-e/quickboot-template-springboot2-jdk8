package com.quickboot.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户持久化实体。
 *
 * <p>映射数据库表 {@code sys_user}，使用 MyBatis-Plus 注解实现主键策略、乐观锁与逻辑删除。
 * 仅在基础设施层使用，不对外暴露。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class UserEntity {

    /** 主键 ID，采用雪花算法自动赋值（ASSIGN_ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户名（登录账号） */
    private String username;

    /** 邮箱地址 */
    private String email;

    /** 昵称 */
    private String nickname;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 逻辑删除标志：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;

    /** 乐观锁版本号 */
    @Version
    private Integer version;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
