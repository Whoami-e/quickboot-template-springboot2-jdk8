package com.quickboot.common.api;

import com.quickboot.common.enums.BaseEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ErrorCode} 枚举单元测试。
 *
 * <p>验证所有错误码枚举项的 code 和 message 值正确，
 * {@link ErrorCode#getDescription()} 与 {@link ErrorCode#getMessage()} 一致，
 * 以及 ErrorCode 同时实现了 {@link IErrorCode} 和 {@link BaseEnum} 接口。
 */
@DisplayName("ErrorCode 错误码枚举测试")
class ErrorCodeTest {

    @Test
    @DisplayName("SUCCESS 枚举项 code=0, message='success'")
    void successEnum() {
        assertThat(ErrorCode.SUCCESS.getCode()).isEqualTo(0);
        assertThat(ErrorCode.SUCCESS.getMessage()).isEqualTo("success");
    }

    @Test
    @DisplayName("BAD_REQUEST 枚举项 code=400, message='bad request'")
    void badRequestEnum() {
        assertThat(ErrorCode.BAD_REQUEST.getCode()).isEqualTo(400);
        assertThat(ErrorCode.BAD_REQUEST.getMessage()).isEqualTo("bad request");
    }

    @Test
    @DisplayName("UNAUTHORIZED 枚举项 code=401, message='unauthorized'")
    void unauthorizedEnum() {
        assertThat(ErrorCode.UNAUTHORIZED.getCode()).isEqualTo(401);
        assertThat(ErrorCode.UNAUTHORIZED.getMessage()).isEqualTo("unauthorized");
    }

    @Test
    @DisplayName("FORBIDDEN 枚举项 code=403, message='forbidden'")
    void forbiddenEnum() {
        assertThat(ErrorCode.FORBIDDEN.getCode()).isEqualTo(403);
        assertThat(ErrorCode.FORBIDDEN.getMessage()).isEqualTo("forbidden");
    }

    @Test
    @DisplayName("NOT_FOUND 枚举项 code=404, message='not found'")
    void notFoundEnum() {
        assertThat(ErrorCode.NOT_FOUND.getCode()).isEqualTo(404);
        assertThat(ErrorCode.NOT_FOUND.getMessage()).isEqualTo("not found");
    }

    @Test
    @DisplayName("TOO_MANY_REQUESTS 枚举项 code=429, message='too many requests'")
    void tooManyRequestsEnum() {
        assertThat(ErrorCode.TOO_MANY_REQUESTS.getCode()).isEqualTo(429);
        assertThat(ErrorCode.TOO_MANY_REQUESTS.getMessage()).isEqualTo("too many requests");
    }

    @Test
    @DisplayName("INTERNAL_ERROR 枚举项 code=500, message='internal server error'")
    void internalErrorEnum() {
        assertThat(ErrorCode.INTERNAL_ERROR.getCode()).isEqualTo(500);
        assertThat(ErrorCode.INTERNAL_ERROR.getMessage()).isEqualTo("internal server error");
    }

    @Test
    @DisplayName("SERVICE_UNAVAILABLE 枚举项 code=503, message='service unavailable'")
    void serviceUnavailableEnum() {
        assertThat(ErrorCode.SERVICE_UNAVAILABLE.getCode()).isEqualTo(503);
        assertThat(ErrorCode.SERVICE_UNAVAILABLE.getMessage()).isEqualTo("service unavailable");
    }

    @ParameterizedTest(name = "{0} 的 getDescription() 应与 getMessage() 一致")
    @EnumSource(ErrorCode.class)
    @DisplayName("所有枚举项 getDescription() 与 getMessage() 返回值一致")
    void getDescriptionEqualsGetMessage(ErrorCode errorCode) {
        assertThat(errorCode.getDescription()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("ErrorCode 实现 IErrorCode 接口")
    void implementsIErrorCode() {
        assertThat(ErrorCode.SUCCESS).isInstanceOf(IErrorCode.class);
        assertThat(ErrorCode.INTERNAL_ERROR).isInstanceOf(IErrorCode.class);
    }

    @Test
    @DisplayName("ErrorCode 实现 BaseEnum 接口")
    void implementsBaseEnum() {
        assertThat(ErrorCode.SUCCESS).isInstanceOf(BaseEnum.class);
        assertThat(ErrorCode.NOT_FOUND).isInstanceOf(BaseEnum.class);
    }

    @Test
    @DisplayName("ErrorCode 共包含 8 个枚举项")
    void enumCount() {
        assertThat(ErrorCode.values()).hasSize(8);
    }
}
