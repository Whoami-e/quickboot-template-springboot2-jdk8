package com.quickboot.web.handler;

import com.quickboot.common.api.ApiResponse;
import com.quickboot.common.api.ErrorCode;
import com.quickboot.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器。
 * <p>
 * 通过 {@code @RestControllerAdvice} 统一拦截 Controller 层抛出的异常，
 * 转换为标准 {@link ApiResponse} 响应体返回，避免向前端暴露堆栈信息。
 * <p>
 * 注意：Sa-Token 相关异常由 {@link SaTokenExceptionHandler} 以更高优先级单独处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常：使用异常自身携带的错误码与消息返回。
     *
     * @param ex 业务异常
     * @return 包含业务错误码的统一响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理参数校验异常：{@code @RequestBody} 校验失败（MethodArgumentNotValidException）
     * 与表单绑定校验失败（BindException），统一返回 BAD_REQUEST（400）。
     *
     * @param ex 校验异常
     * @return 包含 400 错误码的统一响应
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidationException(Exception ex) {
        return ApiResponse.fail(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
    }

    /**
     * 处理请求路径不存在异常：返回 NOT_FOUND（404）。
     *
     * @param ex 未找到处理器异常
     * @return 包含 404 错误码的统一响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResponse<Void> handleNoHandlerFound(NoHandlerFoundException ex) {
        return ApiResponse.fail(ErrorCode.NOT_FOUND.getCode(), ErrorCode.NOT_FOUND.getMessage());
    }

    /**
     * 处理请求方法不支持异常：如对只支持 GET 的接口发起 POST，返回 BAD_REQUEST（400）。
     *
     * @param ex 方法不支持异常
     * @return 包含 400 错误码的统一响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ApiResponse.fail(ErrorCode.BAD_REQUEST.getCode(), ErrorCode.BAD_REQUEST.getMessage());
    }

    /**
     * 兜底处理所有未被前面方法捕获的异常：记录错误日志并返回 INTERNAL_ERROR（500），
     * 避免堆栈信息直接返回给前端。
     *
     * @param ex 未捕获异常
     * @return 包含 500 错误码的统一响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ApiResponse.fail(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage());
    }
}
