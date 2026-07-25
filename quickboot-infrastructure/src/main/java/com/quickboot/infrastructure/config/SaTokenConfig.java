package com.quickboot.infrastructure.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 认证可插拔配置。
 *
 * <p>仅当 classpath 上存在 Sa-Token 核心类（{@code cn.dev33.satoken.stp.StpUtil}），
 * 且 {@code quickboot.sa-token.enabled=true} 时才会装配。</p>
 *
 * <p>由于本模块 pom 中已将 sa-token-spring-boot-starter 声明为 optional 依赖，
 * 编译期可正常解析 Sa-Token 类型；optional 仅影响向下游模块的传递性。
 * 运行时若 web 层未显式引入 Sa-Token，{@link ConditionalOnClass}（name 字符串形式）
 * 会阻止本类加载，不会因 SaInterceptor 缺失而抛出 NoClassDefFoundError。</p>
 *
 * <p>拦截路由：{@code /api/**}；放行健康检查与监控端点：
 * {@code /health}、{@code /actuator/**}。默认 {@code SaInterceptor()} 会对匹配路由
 * 执行 {@code StpUtil.checkLogin()}，即所有 API 接口默认需要登录。</p>
 */
@Configuration // 常规 Spring 配置类
@ConditionalOnClass(name = "cn.dev33.satoken.stp.StpUtil") // classpath 存在 Sa-Token 核心类时才装配（字符串形式，避免类加载）
@ConditionalOnProperty(prefix = "quickboot.sa-token", name = "enabled", havingValue = "true") // quickboot.sa-token.enabled=true 时才装配
public class SaTokenConfig implements WebMvcConfigurer { // 实现 WebMvcConfigurer 以注册拦截器

    /**
     * 注册 Sa-Token 拦截器，对指定路由进行登录校验。
     *
     * <p>拦截策略：</p>
     * <ul>
     *   <li>拦截：{@code /api/**} —— 所有业务 API 接口默认需要登录。</li>
     *   <li>放行：{@code /health}（健康检查）、{@code /actuator/**}（监控端点），
     *       确保基础设施探针和监控不受认证影响。</li>
     * </ul>
     *
     * <p>{@link SaInterceptor} 默认无参构造会对匹配路由执行
     * {@code StpUtil.checkLogin()}，即校验当前请求是否已登录。
     * 如需细粒度权限控制，可在构造时传入自定义的 {@code SaRouteFunction}。</p>
     *
     * @param registry MVC 拦截器注册器，由 Spring 自动传入
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/api/**")            // 拦截所有 API 路由
                .excludePathPatterns("/health", "/actuator/**"); // 放行健康检查与监控端点
    }
}
