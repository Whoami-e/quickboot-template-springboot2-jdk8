package com.quickboot.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应封装。
 *
 * <p>所有接口返回值统一使用此结构，包含状态码 {@code code}、消息 {@code message} 和数据体 {@code data}，
 * 便于前端按统一格式解析处理。
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /** 状态码，0 表示成功 */
    private Integer code;
    /** 响应消息 */
    private String message;
    /** 响应数据 */
    private T data;

    /**
     * 构建成功响应（携带数据）。
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 包含数据的成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /**
     * 构建成功响应（无数据）。
     *
     * @param <T> 数据类型
     * @return 数据为 null 的成功响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 构建失败响应。
     *
     * @param code    错误码
     * @param message 错误信息
     * @param <T>     数据类型
     * @return 数据为 null 的失败响应
     */
    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
