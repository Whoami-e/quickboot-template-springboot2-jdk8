package com.quickboot.common.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 集合工具类。
 *
 * <p>提供常用的集合判空及空集合获取等操作。
 * 该类为工具类，不可实例化。
 */
public final class CollectionUtils {

    /**
     * 私有构造器，阻止外部实例化。
     *
     * <p>工具类仅提供静态方法，不需要也不应该创建实例。
     */
    private CollectionUtils() {
    }

    /**
     * 判断集合是否为 null 或空集合。
     *
     * @param coll 待检查的集合
     * @return 为 null 或空集合时返回 true
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 判断集合是否非 null 且非空集合。
     *
     * @param coll 待检查的集合
     * @return 不为 null 且不为空集合时返回 true
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 获取不可变的空列表。
     *
     * @param <T> 列表元素类型
     * @return 不可变的空列表（单例），可安全共享
     */
    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }
}
