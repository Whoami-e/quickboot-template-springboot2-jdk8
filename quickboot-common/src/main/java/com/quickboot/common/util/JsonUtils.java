package com.quickboot.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON 工具类。
 *
 * <p>基于 Jackson {@link ObjectMapper} 封装常用的 JSON 序列化与反序列化操作。
 * 该类为工具类，不可实例化。
 */
public final class JsonUtils {

    /**
     * ObjectMapper 单例。
     *
     * <p>Jackson 官方文档明确指出 ObjectMapper 是线程安全的，
     * 在完成配置后可被多线程并发共享使用，因此使用静态单例即可。
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 私有构造器，阻止外部实例化。
     *
     * <p>工具类仅提供静态方法，不需要也不应该创建实例。
     */
    private JsonUtils() {
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param obj 要序列化的对象
     * @return JSON 字符串
     * @throws RuntimeException 序列化失败时抛出
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象。
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   目标类型
     * @return 反序列化后的对象
     * @throws RuntimeException 反序列化失败时抛出
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为泛型类型对象。
     *
     * <p>适用于 {@code List<T>}、{@code Map<K, V>} 等带泛型参数的类型，
     * 通过 {@link TypeReference} 保留泛型信息。
     *
     * @param json    JSON 字符串
     * @param typeRef 类型引用，封装了目标泛型类型
     * @param <T>     目标类型
     * @return 反序列化后的对象
     * @throws RuntimeException 反序列化失败时抛出
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }
}
