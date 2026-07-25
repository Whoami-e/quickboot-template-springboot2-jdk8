package com.quickboot.web.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.quickboot.common.api.ApiResponse;
import com.quickboot.common.api.ErrorCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常处理。
 * <p>
 * 编译期依赖 sa-token-spring-boot-starter（web 模块已声明 optional），
 * 运行期由 @ConditionalOnClass 与 @ConditionalOnProperty 双重保护：
 * 仅当 classpath 存在 StpUtil 且 quickboot.sa-token.enabled=true 时生效。
 */
// 最高优先级：使 Sa-Token 异常优先于 GlobalExceptionHandler 的兜底 Exception 处理被匹配，
// 避免鉴权异常被通用处理器吞掉而返回 500
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@ConditionalOnClass(name = "cn.dev33.satoken.stp.StpUtil")
@ConditionalOnProperty(prefix = "quickboot.sa-token", name = "enabled", havingValue = "true")
public class SaTokenExceptionHandler {

    /**
     * 处理未登录异常：访问需要登录的接口但未登录或登录态已过期，返回 UNAUTHORIZED（401）。
     *
     * @param ex 未登录异常
     * @return 提示"未登录或登录已过期"的统一响应
     */
    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<Void> handleNotLogin(NotLoginException ex) {
        return ApiResponse.fail(ErrorCode.UNAUTHORIZED.getCode(), "未登录或登录已过期");
    }

    /**
     * 处理无权限异常：当前登录用户缺少访问目标资源所需的权限码，返回 FORBIDDEN（403）。
     *
     * @param ex 无权限异常
     * @return 提示"无权限访问"的统一响应
     */
    @ExceptionHandler(NotPermissionException.class)
    public ApiResponse<Void> handleNotPermission(NotPermissionException ex) {
        return ApiResponse.fail(ErrorCode.FORBIDDEN.getCode(), "无权限访问");
    }

    /**
     * 处理无角色异常：当前登录用户缺少访问目标资源所需的角色，返回 FORBIDDEN（403）。
     *
     * @param ex 无角色异常
     * @return 提示"无角色权限"的统一响应
     */
    @ExceptionHandler(NotRoleException.class)
    public ApiResponse<Void> handleNotRole(NotRoleException ex) {
        return ApiResponse.fail(ErrorCode.FORBIDDEN.getCode(), "无角色权限");
    }
}
