package com.quickboot.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域 CORS 配置。
 * <p>
 * 实现 {@link WebMvcConfigurer}，通过 {@code addCorsMappings} 注册全局跨域规则。
 * <p>
 * 条件装配：受 {@code quickboot.cors.enabled} 控制，默认（缺省）开启，
 * 设为 false 可在无需跨域的场景下彻底关闭。
 */
@Configuration
@ConditionalOnProperty(prefix = "quickboot.cors", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CorsConfig implements WebMvcConfigurer {

    @Value("${quickboot.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${quickboot.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    /**
     * 注册全局 CORS 跨域映射。
     * <p>
     * 当 allowedOrigins 为 "*" 时，由于 Spring Boot 2.7+ 不允许 {@code allowedOrigins("*")}
     * 与 {@code allowCredentials(true)} 同时使用（浏览器规范要求凭证模式下源必须明确指定），
     * 故改用 {@code allowedOriginPatterns("*")} 兼容带凭证的跨域请求；
     * 当配置为具体域名时，直接使用 {@code allowedOrigins} 精确匹配，安全性更高。
     *
     * @param registry CORS 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if ("*".equals(allowedOrigins)) {
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods(allowedMethods.split(","))
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        } else {
            registry.addMapping("/**")
                    .allowedOrigins(allowedOrigins.split(","))
                    .allowedMethods(allowedMethods.split(","))
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }
}
