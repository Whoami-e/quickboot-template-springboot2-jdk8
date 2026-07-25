package com.quickboot.common.util;

/**
 * 字符串工具类。
 *
 * <p>提供常用的字符串判空、判空白以及驼峰/下划线互转等操作。
 * 该类为工具类，不可实例化。
 */
public final class StringUtils {

    /**
     * 私有构造器，阻止外部实例化。
     *
     * <p>工具类仅提供静态方法，不需要也不应该创建实例。
     */
    private StringUtils() {
    }

    /**
     * 判断字符串是否为 null 或空字符串（长度为 0）。
     *
     * @param str 待检查的字符串
     * @return 为 null 或空字符串时返回 true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否非 null 且非空字符串。
     *
     * @param str 待检查的字符串
     * @return 不为 null 且不为空字符串时返回 true
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为 null 或全为空白字符（空格、制表符等）。
     *
     * @param str 待检查的字符串
     * @return 为 null 或全为空白字符时返回 true
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否非 null 且非全空白字符。
     *
     * @param str 待检查的字符串
     * @return 不为 null 且不全为空白字符时返回 true
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 将驼峰命名转换为下划线命名。
     *
     * <p>例如 {@code userName} 转换为 {@code user_name}，
     * {@code HTTPResponse} 转换为 {@code h_t_t_p_response}。
     *
     * @param str 驼峰命名字符串
     * @return 下划线命名字符串，输入为空时原样返回
     */
    public static String camelToUnderline(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将下划线命名转换为驼峰命名。
     *
     * <p>例如 {@code user_name} 转换为 {@code userName}，
     * {@code created_at} 转换为 {@code createdAt}。
     *
     * @param str 下划线命名字符串
     * @return 驼峰命名字符串，输入为空时原样返回
     */
    public static String underlineToCamel(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                upperNext = true;
            } else if (upperNext) {
                sb.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
