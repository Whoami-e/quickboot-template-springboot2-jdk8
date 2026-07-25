package com.quickboot.common.exception;

import com.quickboot.common.api.ErrorCode;
import com.quickboot.common.api.IErrorCode;

/**
 * 业务异常。
 *
 * <p>用于在业务逻辑中抛出可预知的异常，携带错误码以便全局异常处理器返回对应的错误响应。
 * 继承 {@link RuntimeException}，属于非受检异常，无需在方法签名中声明。
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final Integer code;

    /**
     * 根据错误信息构建异常，错误码默认为 {@link ErrorCode#BAD_REQUEST}。
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.BAD_REQUEST.getCode();
    }

    /**
     * 根据错误码和错误信息构建异常。
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 根据 {@link IErrorCode} 构建异常，从中提取错误码和错误信息。
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 获取错误码。
     *
     * @return 错误码数值
     */
    public Integer getCode() {
        return code;
    }
}
