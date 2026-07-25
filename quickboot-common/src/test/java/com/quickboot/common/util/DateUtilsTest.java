package com.quickboot.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * {@link DateUtils} 单元测试。
 *
 * <p>验证日期时间工具类的格式化、解析和获取当前时间操作：
 * {@link DateUtils#format(LocalDateTime)}、{@link DateUtils#format(LocalDateTime, String)}、
 * {@link DateUtils#parse(String)}、{@link DateUtils#parse(String, String)}、{@link DateUtils#now()}。
 */
@DisplayName("DateUtils 日期时间工具类测试")
class DateUtilsTest {

    @Test
    @DisplayName("format() 使用默认格式 yyyy-MM-dd HH:mm:ss")
    void formatWithDefaultPattern() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);

        String result = DateUtils.format(dateTime);

        assertThat(result).isEqualTo("2024-01-15 10:30:45");
    }

    @Test
    @DisplayName("format() 使用自定义格式 yyyy-MM-dd")
    void formatWithCustomPattern() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 1, 8, 0, 0);

        String result = DateUtils.format(dateTime, DateUtils.DATE_PATTERN);

        assertThat(result).isEqualTo("2024-06-01");
    }

    @Test
    @DisplayName("format() 使用自定义格式 yyyy/MM/dd HH:mm")
    void formatWithSlashPattern() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 31, 23, 59, 0);

        String result = DateUtils.format(dateTime, "yyyy/MM/dd HH:mm");

        assertThat(result).isEqualTo("2024/12/31 23:59");
    }

    @Test
    @DisplayName("format(null) 返回 null")
    void formatNullReturnsNull() {
        assertThat(DateUtils.format(null)).isNull();
        assertThat(DateUtils.format(null, "yyyy-MM-dd")).isNull();
    }

    @Test
    @DisplayName("parse() 使用默认格式 yyyy-MM-dd HH:mm:ss")
    void parseWithDefaultPattern() {
        LocalDateTime result = DateUtils.parse("2024-01-15 10:30:45");

        assertThat(result).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30, 45));
    }

    @Test
    @DisplayName("parse() 使用自定义格式 yyyy-MM-dd HH:mm")
    void parseWithCustomPattern() {
        LocalDateTime result = DateUtils.parse("2024-06-01 08:30", "yyyy-MM-dd HH:mm");

        assertThat(result).isEqualTo(LocalDateTime.of(2024, 6, 1, 8, 30, 0));
    }

    @Test
    @DisplayName("parse() 日期-only 格式无法直接解析为 LocalDateTime（抛出 DateTimeParseException）")
    void parseDateOnlyThrowsException() {
        assertThatThrownBy(() -> DateUtils.parse("2024-06-01", DateUtils.DATE_PATTERN))
                .isInstanceOf(java.time.format.DateTimeParseException.class);
    }

    @Test
    @DisplayName("parse() 自定义格式 yyyy/MM/dd HH:mm:ss")
    void parseWithSlashPattern() {
        LocalDateTime result = DateUtils.parse("2024/12/31 23:59:59", "yyyy/MM/dd HH:mm:ss");

        assertThat(result).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59, 59));
    }

    @Test
    @DisplayName("parse(null) 和 parse('') 返回 null")
    void parseNullOrEmptyReturnsNull() {
        assertThat(DateUtils.parse(null)).isNull();
        assertThat(DateUtils.parse("")).isNull();
        assertThat(DateUtils.parse(null, "yyyy-MM-dd")).isNull();
        assertThat(DateUtils.parse("", "yyyy-MM-dd")).isNull();
    }

    @Test
    @DisplayName("now() 返回当前时间")
    void nowReturnsCurrentTime() {
        LocalDateTime before = LocalDateTime.now();

        LocalDateTime result = DateUtils.now();

        LocalDateTime after = LocalDateTime.now();

        assertThat(result)
                .isBetween(before, after)
                .isCloseTo(before, within(1, ChronoUnit.SECONDS));
    }
}
