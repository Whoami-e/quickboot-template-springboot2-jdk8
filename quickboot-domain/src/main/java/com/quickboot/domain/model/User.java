package com.quickboot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户领域模型。
 *
 * <p>表示系统中的用户实体，承载用户基本信息及状态。
 * 作为领域层纯 POJO，不依赖任何持久化框架注解。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** 用户唯一标识 */
    private Long id;
    /** 用户名（登录账号） */
    private String username;
    /** 邮箱地址 */
    private String email;
    /** 昵称 */
    private String nickname;
    /** 状态：0-禁用，1-启用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
