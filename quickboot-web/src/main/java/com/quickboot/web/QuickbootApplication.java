package com.quickboot.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * QuickBoot 应用启动类。
 * <p>
 * 通过 {@code scanBasePackages = "com.quickboot"} 扫描多模块下所有 com.quickboot 包的组件。
 * <p>
 * 排除以下自动装配类，使项目在最小依赖下即可启动，按需通过配置开关开启：
 * <ul>
 *   <li>{@link DataSourceAutoConfiguration} —— 排除数据源自动装配：模板默认不引入数据库依赖，
 *       避免启动时因找不到 DataSource 配置而报错；接入 MyBatis-Plus 时由 infrastructure 模块自行装配</li>
 *   <li>{@link RedisAutoConfiguration} —— 排除 Redis 自动装配：模板默认不强制依赖 Redis，
 *       由 quickboot.redis.enabled 开关控制是否启用，避免无 Redis 环境下启动失败</li>
 *   <li>{@link RedisRepositoriesAutoConfiguration} —— 排除 Redis 仓储自动装配：同上，
 *       关闭 Redis Repository 机制，避免在未配置 Redis 时尝试初始化仓储</li>
 * </ul>
 */
@SpringBootApplication(
        scanBasePackages = "com.quickboot",
        exclude = {
                DataSourceAutoConfiguration.class,
                RedisAutoConfiguration.class,
                RedisRepositoriesAutoConfiguration.class
        }
)
public class QuickbootApplication {
    /**
     * 应用入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(QuickbootApplication.class, args);
    }
}
