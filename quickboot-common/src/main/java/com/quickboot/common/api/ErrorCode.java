package com.quickboot.common.api;

import com.quickboot.common.enums.BaseEnum;

/**
 * 系统通用错误码枚举。
 *
 * <p>同时实现 {@link IErrorCode}（提供错误码与错误信息）和 {@link BaseEnum}（提供枚举序列化描述），
 * 便于在全局异常处理、API 响应封装以及枚举序列化等场景统一引用。
 */
public enum ErrorCode implements IErrorCode, BaseEnum {
    /** 请求成功 */
    SUCCESS(0, "success"),
    /** 错误请求（参数不合法等） */
    BAD_REQUEST(400, "bad request"),
    /** 服务器内部错误 */
    INTERNAL_ERROR(500, "internal server error"),
    /** 未认证（需要登录） */
    UNAUTHORIZED(401, "unauthorized"),
    /** 无权限访问 */
    FORBIDDEN(403, "forbidden"),
    /** 资源不存在 */
    NOT_FOUND(404, "not found"),
    /** 请求过于频繁（限流） */
    TOO_MANY_REQUESTS(429, "too many requests"),
    /** 服务不可用 */
    SERVICE_UNAVAILABLE(503, "service unavailable"),
    /** 用户不存在 */
    USER_NOT_FOUND(1001, "用户不存在"),
    /** 用户已存在 */
    USER_ALREADY_EXISTS(1002, "用户已存在");

    /** 错误码 */
    private final Integer code;
    /** 错误信息 */
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码。
     *
     * @return 错误码数值
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取错误信息。
     *
     * @return 错误信息文本
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取枚举描述信息，用于序列化等场景。
     *
     * @return 与 {@link #message} 相同的描述文本
     */
    @Override
    public String getDescription() {
        return this.message;
    }
}
