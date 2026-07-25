package com.quickboot.common.exception;

import com.quickboot.common.api.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link BusinessException} 单元测试。
 *
 * <p>验证三个构造器的行为：
 * <ol>
 *   <li>{@link BusinessException#BusinessException(String)} —— 默认 code 为 400</li>
 *   <li>{@link BusinessException#BusinessException(Integer, String)} —— 自定义 code 和 message</li>
 *   <li>{@link BusinessException#BusinessException(com.quickboot.common.api.IErrorCode)} —— 从 ErrorCode 提取 code/message</li>
 * </ol>
 * 同时验证 BusinessException 作为 RuntimeException 可被抛出和捕获。
 */
@DisplayName("BusinessException 业务异常测试")
class BusinessExceptionTest {

    @Test
    @DisplayName("BusinessException(String) 构造器：code 默认为 400")
    void constructorWithMessageOnly() {
        BusinessException ex = new BusinessException("参数不合法");

        assertThat(ex.getCode()).isEqualTo(400);
        assertThat(ex.getMessage()).isEqualTo("参数不合法");
    }

    @Test
    @DisplayName("BusinessException(String) code 与 ErrorCode.BAD_REQUEST.getCode() 一致")
    void constructorDefaultCodeMatchesBadRequest() {
        BusinessException ex = new BusinessException("test");

        assertThat(ex.getCode()).isEqualTo(ErrorCode.BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("BusinessException(Integer, String) 构造器：携带自定义 code 和 message")
    void constructorWithCodeAndMessage() {
        BusinessException ex = new BusinessException(4001, "用户不存在");

        assertThat(ex.getCode()).isEqualTo(4001);
        assertThat(ex.getMessage()).isEqualTo("用户不存在");
    }

    @Test
    @DisplayName("BusinessException(IErrorCode) 构造器：code 和 message 来自 ErrorCode")
    void constructorWithErrorCode() {
        BusinessException ex = new BusinessException(ErrorCode.NOT_FOUND);

        assertThat(ex.getCode()).isEqualTo(404);
        assertThat(ex.getMessage()).isEqualTo("not found");
    }

    @Test
    @DisplayName("BusinessException(IErrorCode) 使用 INTERNAL_ERROR")
    void constructorWithInternalError() {
        BusinessException ex = new BusinessException(ErrorCode.INTERNAL_ERROR);

        assertThat(ex.getCode()).isEqualTo(500);
        assertThat(ex.getMessage()).isEqualTo("internal server error");
    }

    @Test
    @DisplayName("BusinessException 是 RuntimeException 子类，可被 try-catch 捕获")
    void isRuntimeException() {
        assertThatThrownBy(() -> {
            throw new BusinessException("业务异常");
        }).isInstanceOf(RuntimeException.class)
          .isInstanceOf(BusinessException.class)
          .hasMessage("业务异常");

        assertThat(BusinessException.class.getSuperclass())
                .isEqualTo(RuntimeException.class);
    }
}
