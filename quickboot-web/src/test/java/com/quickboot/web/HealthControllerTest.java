package com.quickboot.web;

import com.quickboot.application.service.HealthService;
import com.quickboot.common.api.ErrorCode;
import com.quickboot.domain.model.HealthStatus;
import com.quickboot.web.config.JacksonConfig;
import com.quickboot.web.controller.HealthController;
import com.quickboot.web.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link HealthController} Web 层切片测试。
 *
 * <p>使用 {@code @WebMvcTest} 仅加载 Web 层组件（Controller + JacksonConfig + 异常处理器），
 * 通过 {@code @MockBean} 隔离 Service 层，使用 MockMvc 验证 HTTP 响应结构。
 *
 * <p>验证要点：
 * <ul>
 *   <li>GET /health 返回 HTTP 200</li>
 *   <li>响应 JSON 中 code=0（成功）</li>
 *   <li>响应 JSON 中 data.status="UP"</li>
 *   <li>响应 JSON 中 data.application="quickboot"</li>
 * </ul>
 */
@WebMvcTest(HealthController.class)
@Import({JacksonConfig.class, GlobalExceptionHandler.class})
@DisplayName("HealthController 健康检查接口测试")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthService healthService;

    @BeforeEach
    void setUp() {
        when(healthService.check()).thenReturn(
                new HealthStatus("UP", "quickboot", LocalDateTime.of(2024, 1, 1, 0, 0, 0)));
    }

    @Test
    @DisplayName("Spring MVC 上下文加载正常（回归闸门）")
    void contextLoads() {
        // @WebMvcTest 切片上下文加载验证：HealthController + JacksonConfig + GlobalExceptionHandler 均可装配
    }

    @Test
    @DisplayName("GET /health 返回 HTTP 200")
    void healthEndpointReturns200() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("响应 JSON 中 code=0 表示成功")
    void healthEndpointReturnsSuccessCode() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()));
    }

    @Test
    @DisplayName("响应 JSON 中 message='success'")
    void healthEndpointReturnsSuccessMessage() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    @DisplayName("响应 JSON 中 data.status='UP'")
    void healthEndpointReturnsStatusUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    @DisplayName("响应 JSON 中 data.application='quickboot'")
    void healthEndpointReturnsApplicationName() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(jsonPath("$.data.application").value("quickboot"));
    }

    @Test
    @DisplayName("完整响应结构验证：code + message + data 三层结构正确")
    void healthEndpointFullResponseStructure() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.application").value("quickboot"))
                .andExpect(jsonPath("$.data.checkedAt").exists());
    }
}
