package com.quickboot.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 全局 Jackson 序列化配置。
 * <p>
 * 通过 {@link Jackson2ObjectMapperBuilderCustomizer} 对 Spring Boot 自动装配的 ObjectMapper 进行定制，
 * 统一全工程的 JSON 序列化/反序列化行为，无需手动创建并覆盖 ObjectMapper Bean。
 * <p>
 * 本类为无条件 {@code @Configuration}（未添加任何条件装配注解），随容器启动即生效，
 * 因为 JSON 处理是 Web 层的基础能力，主配置始终需要。
 */
@Configuration
public class JacksonConfig {

    /**
     * 注册 Jackson 定制器，统一全局序列化策略。
     * <p>
     * 采用 Customizer 而非直接定义 ObjectMapper Bean，可避免覆盖 Spring Boot 默认配置，
     * 同时保证其它模块/自动装配对 ObjectMapper 的定制仍能叠加生效。
     *
     * @return Jackson2ObjectMapperBuilder 定制逻辑
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 反序列化时忽略未知字段：前端传入多余字段不报错，保证接口向前兼容
            builder.failOnUnknownProperties(false);
            // 禁止将日期序列化为时间戳（毫秒数）：时间戳可读性差且各端处理不一致，统一使用格式化字符串
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // LocalDateTime / LocalDate / LocalTime 序列化格式：统一为标准易读格式，避免各端格式不一致
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            builder.serializers(new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
            // Long 转 String：JS Number 最大安全整数为 2^53-1，而 Java Long 可达 2^63-1，
            // 雪花 ID 等超出安全范围的值传到前端会丢失精度，故统一序列化为字符串
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            // 序列化时忽略 null 字段：减少响应体体积，前端无需处理空字段
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            // 统一时区为 Asia/Shanghai：避免不同部署环境默认时区差异导致时间错乱
            builder.timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        };
    }
}
