package com.quickboot.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类。
 *
 * <p>基于 Java 8 {@link LocalDateTime} 封装常用的日期格式化与解析操作。
 * 该类为工具类，不可实例化。
 */
public final class DateUtils {

    /** 默认日期时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** 日期格式：yyyy-MM-dd */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 私有构造器，阻止外部实例化。
     *
     * <p>工具类仅提供静态方法，不需要也不应该创建实例。
     */
    private DateUtils() {
    }

    /**
     * 使用默认格式（yyyy-MM-dd HH:mm:ss）格式化日期时间。
     *
     * @param dateTime 日期时间对象
     * @return 格式化后的字符串，dateTime 为 null 时返回 null
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_PATTERN);
    }

    /**
     * 使用指定格式格式化日期时间。
     *
     * @param dateTime 日期时间对象
     * @param pattern  日期格式，如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串，dateTime 为 null 时返回 null
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 使用默认格式（yyyy-MM-dd HH:mm:ss）解析日期字符串。
     *
     * @param dateTime 日期字符串
     * @return 解析后的 LocalDateTime，字符串为空时返回 null
     */
    public static LocalDateTime parse(String dateTime) {
        return parse(dateTime, DEFAULT_PATTERN);
    }

    /**
     * 使用指定格式解析日期字符串。
     *
     * @param dateTime 日期字符串
     * @param pattern  日期格式，如 "yyyy-MM-dd HH:mm:ss"
     * @return 解析后的 LocalDateTime，字符串为 null 或空时返回 null
     */
    public static LocalDateTime parse(String dateTime, String pattern) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取当前日期时间。
     *
     * @return 当前系统时间的 LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
