package com.quickboot.web;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * QuickBoot 全量 Spring 上下文加载测试（回归闸门）。
 *
 * <p>使用 {@code @SpringBootTest} 加载完整应用上下文（所有模块的 Bean、自动装配、条件装配等），
 * 验证多模块项目在最小依赖下（排除 DataSource / Redis / Sa-Token）仍可正常启动。
 *
 * <p>该测试不验证具体业务逻辑，仅作为"上下文能否成功初始化"的回归闸门，
 * 若此测试失败通常意味着 Bean 定义、依赖注入或自动装配出现了破坏性变更。
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("QuickBoot 全量上下文加载测试")
class QuickbootContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("全量 Spring 上下文加载正常（回归闸门）")
    void contextLoads() {
        // 验证上下文已成功初始化
        assertThat(applicationContext).isNotNull();
        // 验证关键 Bean 已注册
        assertThat(applicationContext.containsBean("healthController")).isTrue();
        assertThat(applicationContext.containsBean("healthService")).isTrue();
    }
}
