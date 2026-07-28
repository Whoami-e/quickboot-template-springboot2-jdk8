package com.quickboot.web.handler;

import com.quickboot.common.api.ApiResponse;
import com.quickboot.common.api.ErrorCode;
import com.quickboot.common.exception.BusinessException;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link GlobalExceptionHandler} 单元测试。
 *
 * <p>直接实例化 GlobalExceptionHandler，调用各异常处理方法验证返回的 {@link ApiResponse} 结构，
 * 无需 Spring 上下文或 MockMvc，聚焦于异常处理逻辑本身。
 *
 * <p>验证要点：
 * <ul>
 *   <li>BusinessException 返回异常自身的 code 和 message</li>
 *   <li>通用 Exception 返回 500（INTERNAL_ERROR）</li>
 *   <li>NoHandlerFoundException 返回 404</li>
 *   <li>HttpRequestMethodNotSupportedException 返回 400</li>
 * </ul>
 */
@DisplayName("GlobalExceptionHandler 全局异常处理器测试")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);

        handler = new GlobalExceptionHandler();
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.detachAppender(logAppender);
    }

    @Test
    @DisplayName("BusinessException 返回异常自身的 code 和 message")
    void handleBusinessException() {
        BusinessException ex = new BusinessException(4001, "用户不存在");

        ApiResponse<Void> response = handler.handleBusinessException(ex);

        assertThat(response.getCode()).isEqualTo(4001);
        assertThat(response.getMessage()).isEqualTo("用户不存在");
        assertThat(response.getData()).isNull();
        assertThat(logAppender.list)
                .anySatisfy(event -> assertThat(event.getLevel()).isEqualTo(Level.WARN));
    }

    @Test
    @DisplayName("BusinessException(ErrorCode.NOT_FOUND) 返回 404")
    void handleBusinessExceptionWithNotFound() {
        BusinessException ex = new BusinessException(ErrorCode.NOT_FOUND);

        ApiResponse<Void> response = handler.handleBusinessException(ex);

        assertThat(response.getCode()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("not found");
        assertThat(logAppender.list)
                .anySatisfy(event -> assertThat(event.getLevel()).isEqualTo(Level.WARN));
    }

    @Test
    @DisplayName("通用 Exception 返回 500 (INTERNAL_ERROR)")
    void handleException() {
        Exception ex = new RuntimeException("未知错误");

        ApiResponse<Void> response = handler.handleException(ex);

        assertThat(response.getCode()).isEqualTo(500);
        assertThat(response.getMessage()).isEqualTo("internal server error");
        assertThat(response.getData()).isNull();
        assertThat(logAppender.list)
                .anySatisfy(event -> assertThat(event.getLevel()).isEqualTo(Level.ERROR));
    }

    @Test
    @DisplayName("NoHandlerFoundException 返回 404 (NOT_FOUND)")
    void handleNoHandlerFound() throws Exception {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/nonexistent", null);

        ApiResponse<Void> response = handler.handleNoHandlerFound(ex);

        assertThat(response.getCode()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("not found");
        assertThat(logAppender.list)
                .anySatisfy(event -> assertThat(event.getLevel()).isEqualTo(Level.WARN));
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 返回 400 (BAD_REQUEST)")
    void handleMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("DELETE");

        ApiResponse<Void> response = handler.handleMethodNotSupported(ex);

        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("bad request");
        assertThat(logAppender.list)
                .anySatisfy(event -> assertThat(event.getLevel()).isEqualTo(Level.WARN));
    }

    @Test
    @DisplayName("参数校验异常返回 400 (BAD_REQUEST) 且 message 为异常自身消息")
    void handleValidationException() {
        // 使用 BindException（表单绑定校验失败）进行测试
        org.springframework.validation.BindException ex =
                new org.springframework.validation.BindException(new Object(), "target");

        ApiResponse<Void> response = handler.handleValidationException(ex);

        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo(ex.getMessage());
        assertThat(response.getData()).isNull();
        assertThat(logAppender.list)
                .anySatisfy(event -> assertThat(event.getLevel()).isEqualTo(Level.WARN));
    }
}
