package com.quickboot.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 缓存可插拔配置。
 *
 * <p>本类通过 {@link ConditionalOnClass} + {@link ConditionalOnProperty} 双重条件
 * 实现三种运行时状态：</p>
 * <ul>
 *   <li><b>未引入依赖</b>：web 层 pom 未引入 spring-boot-starter-data-redis，
 *       classpath 上不存在 {@code RedisOperations} 类。Spring 通过 ASM 读取
 *       {@link ConditionalOnClass} 的 {@code name}（字符串形式）判定条件不满足，
 *       跳过本类加载，不会触发 {@code NoClassDefFoundError}。</li>
 *   <li><b>引入未启用</b>：依赖已引入但 {@code quickboot.redis.enabled} 未设为
 *       {@code true}（或设为 {@code false}），{@link ConditionalOnProperty} 不满足，
 *       本配置类被跳过；但 Spring Boot 的 RedisAutoConfiguration 仍可能生效。</li>
 *   <li><b>启用</b>：依赖已引入且 {@code quickboot.redis.enabled=true}，
 *       本配置类装配，注册自定义 RedisTemplate 和 StringRedisTemplate。</li>
 * </ul>
 *
 * <p>{@link EnableCaching} 开启 Spring 声明式缓存支持，使 {@code @Cacheable}、
 * {@code @CacheEvict} 等注解生效，底层由 Redis 提供缓存存储。</p>
 *
 * <p><b>序列化策略</b>：RedisTemplate 的 key 统一使用 {@link StringRedisSerializer}
 * （保证 key 可读性），value 使用 {@link GenericJackson2JsonRedisSerializer}
 * （将对象序列化为 JSON 并携带类型信息，支持反序列化时自动还原为原始类型）。</p>
 *
 * <p><b>为什么用 @Bean 方法参数注入 RedisConnectionFactory 而非类字段？</b>
 * 因为 {@code RedisConnectionFactory} 属于 Redis 依赖中的类型。若将其声明为类字段，
 * JVM 在加载本类时会触发该类型的类加载；当 Redis 依赖不存在时，
 * 会导致 {@code NoClassDefFoundError}。而使用 {@code @Bean} 方法参数注入，
 * 仅在条件满足、本类被实例化时才会解析参数类型，配合
 * {@link ConditionalOnClass}（字符串形式 name）可安全跳过。</p>
 */
@Configuration
@EnableCaching // 开启 Spring 声明式缓存支持（@Cacheable / @CacheEvict 等注解生效）
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisOperations") // classpath 存在 RedisOperations 类时才装配（字符串形式，避免类加载）
@ConditionalOnProperty(prefix = "quickboot.redis", name = "enabled", havingValue = "true") // quickboot.redis.enabled=true 时才装配
public class RedisConfig {

    /**
     * 通用 RedisTemplate：key 使用 String 序列化，value 使用 Jackson JSON 序列化。
     *
     * <p>序列化策略说明：</p>
     * <ul>
     *   <li>key / hashKey → {@link StringRedisSerializer}：以 String 存储，保证 key 在
     *       Redis CLI 中可读，避免二进制乱码。</li>
     *   <li>value / hashValue → {@link GenericJackson2JsonRedisSerializer}：将对象序列化为
     *       JSON，并携带 {@code @class} 类型信息，反序列化时自动还原为原始类型。</li>
     * </ul>
     *
     * @param connectionFactory 由 Spring Boot 自动装配注入的 Redis 连接工厂
     * @return 配置好序列化器的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory); // 注入由 Spring Boot 自动配置的连接工厂

        // key 与 hashKey 统一使用 String 序列化，保证 Redis 中 key 的可读性
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // value 与 hashValue 使用 JSON 序列化，携带类型信息便于反序列化还原
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(stringSerializer);       // key -> String
        template.setHashKeySerializer(stringSerializer);   // hashKey -> String
        template.setValueSerializer(jsonSerializer);      // value -> JSON
        template.setHashValueSerializer(jsonSerializer);  // hashValue -> JSON
        template.afterPropertiesSet(); // 初始化序列化器等内部状态，必须调用
        return template;
    }

    /**
     * 专用于字符串操作的 StringRedisTemplate，沿用默认的 String 序列化策略。
     *
     * <p>StringRedisTemplate 是 RedisTemplate&lt;String, String&gt; 的子类，
     * key 和 value 均使用 String 序列化，适用于计数器、简单缓存等纯字符串场景。</p>
     *
     * @param connectionFactory 由 Spring Boot 自动装配注入的 Redis 连接工厂
     * @return StringRedisTemplate 实例
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory); // 注入由 Spring Boot 自动配置的连接工厂
        template.afterPropertiesSet(); // 初始化内部状态，必须调用
        return template;
    }
}
