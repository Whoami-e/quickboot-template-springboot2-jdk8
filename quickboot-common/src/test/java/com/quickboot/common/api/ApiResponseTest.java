package com.quickboot.common.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ApiResponse} 单元测试。
 *
 * <p>验证统一响应封装的三个静态工厂方法：
 * {@link ApiResponse#success(Object)}、{@link ApiResponse#success()}、{@link ApiResponse#fail(Integer, String)}。
 * 确保成功响应的 code=0、message="success"，失败响应正确携带传入的 code 和 message，且 data=null。
 */
@DisplayName("ApiResponse 统一响应封装测试")
class ApiResponseTest {

    @Nested
    @DisplayName("success(T data) 携带数据的成功响应")
    class SuccessWithData {

        @Test
        @DisplayName("返回 code=0, message='success', data=输入值")
        void shouldReturnSuccessResponseWithData() {
            String data = "hello";
            ApiResponse<String> response = ApiResponse.success(data);

            assertThat(response.getCode()).isEqualTo(0);
            assertThat(response.getMessage()).isEqualTo("success");
            assertThat(response.getData()).isEqualTo("hello");
        }

        @Test
        @DisplayName("data 为复杂对象时正确携带")
        void shouldCarryComplexObject() {
            java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
            data.put("id", 1);
            data.put("name", "quickboot");
            ApiResponse<java.util.Map<String, Object>> response = ApiResponse.success(data);

            assertThat(response.getCode()).isEqualTo(0);
            assertThat(response.getMessage()).isEqualTo("success");
            assertThat(response.getData()).isSameAs(data);
        }
    }

    @Nested
    @DisplayName("success() 无数据的成功响应")
    class SuccessWithoutData {

        @Test
        @DisplayName("返回 code=0, message='success', data=null")
        void shouldReturnSuccessResponseWithNullData() {
            ApiResponse<Void> response = ApiResponse.success();

            assertThat(response.getCode()).isEqualTo(0);
            assertThat(response.getMessage()).isEqualTo("success");
            assertThat(response.getData()).isNull();
        }
    }

    @Nested
    @DisplayName("fail(code, message) 失败响应")
    class FailResponse {

        @Test
        @DisplayName("返回传入的 code 和 message, data=null")
        void shouldReturnFailResponseWithCodeAndMessage() {
            ApiResponse<Void> response = ApiResponse.fail(404, "not found");

            assertThat(response.getCode()).isEqualTo(404);
            assertThat(response.getMessage()).isEqualTo("not found");
            assertThat(response.getData()).isNull();
        }

        @Test
        @DisplayName("使用 ErrorCode 构造失败响应")
        void shouldReturnFailResponseFromErrorCode() {
            ApiResponse<Void> response = ApiResponse.fail(
                    ErrorCode.INTERNAL_ERROR.getCode(),
                    ErrorCode.INTERNAL_ERROR.getMessage());

            assertThat(response.getCode()).isEqualTo(500);
            assertThat(response.getMessage()).isEqualTo("internal server error");
            assertThat(response.getData()).isNull();
        }
    }
}
