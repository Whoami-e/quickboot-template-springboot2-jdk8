package com.quickboot.web.dto;

import lombok.Data;

import javax.validation.constraints.Email;

/**
 * 更新用户请求 DTO。
 *
 * <p>封装客户端更新用户时提交的参数，所有字段均为可选，仅更新非空字段。
 * 使用 JSR-303 注解对非空字段进行格式校验。
 */
@Data
public class UpdateUserRequest {

    /** 邮箱地址（可选，若提供则必须符合邮箱格式） */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 昵称（可选） */
    private String nickname;
}
