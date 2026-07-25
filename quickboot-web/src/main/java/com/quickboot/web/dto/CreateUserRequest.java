package com.quickboot.web.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 创建用户请求 DTO。
 *
 * <p>封装客户端创建用户时提交的参数，使用 JSR-303 注解进行参数校验。
 */
@Data
public class CreateUserRequest {

    /** 用户名（必填，不能为空） */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 邮箱地址（必须符合邮箱格式） */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 昵称（可选） */
    private String nickname;
}
