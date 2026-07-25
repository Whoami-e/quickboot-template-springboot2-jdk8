package com.quickboot.common.api;

/**
 * 错误码接口。
 *
 * <p>定义统一的错误码抽象，所有业务错误码类型（如枚举）均应实现此接口，
 * 以便全局异常处理器等组件通过统一的方式获取错误码与错误信息。
 */
public interface IErrorCode {

    /**
     * 获取错误码。
     *
     * @return 错误码数值
     */
    Integer getCode();

    /**
     * 获取错误信息描述。
     *
     * @return 错误信息文本
     */
    String getMessage();
}
