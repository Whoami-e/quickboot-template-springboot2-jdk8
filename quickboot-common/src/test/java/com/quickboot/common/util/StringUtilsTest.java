package com.quickboot.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link StringUtils} 单元测试。
 *
 * <p>验证字符串工具类的判空、判空白以及驼峰/下划线互转操作。
 */
@DisplayName("StringUtils 字符串工具类测试")
class StringUtilsTest {

    @Nested
    @DisplayName("isEmpty / isNotEmpty")
    class IsEmptyTest {

        @Test
        @DisplayName("isEmpty(null) 返回 true")
        void isEmptyNull() {
            assertThat(StringUtils.isEmpty(null)).isTrue();
        }

        @Test
        @DisplayName("isEmpty('') 返回 true")
        void isEmptyEmptyString() {
            assertThat(StringUtils.isEmpty("")).isTrue();
        }

        @Test
        @DisplayName("isEmpty('hello') 返回 false")
        void isEmptyNonEmpty() {
            assertThat(StringUtils.isEmpty("hello")).isFalse();
        }

        @Test
        @DisplayName("isEmpty(' ') 返回 false（空格不是空字符串）")
        void isEmptyWhitespace() {
            assertThat(StringUtils.isEmpty(" ")).isFalse();
        }

        @Test
        @DisplayName("isNotEmpty(null) 返回 false")
        void isNotEmptyNull() {
            assertThat(StringUtils.isNotEmpty(null)).isFalse();
        }

        @Test
        @DisplayName("isNotEmpty('hello') 返回 true")
        void isNotEmptyNonEmpty() {
            assertThat(StringUtils.isNotEmpty("hello")).isTrue();
        }
    }

    @Nested
    @DisplayName("isBlank / isNotBlank")
    class IsBlankTest {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("isBlank(null) 和 isBlank('') 返回 true")
        void isBlankNullOrEmpty(String str) {
            assertThat(StringUtils.isBlank(str)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "\t", "\n", " \t\n "})
        @DisplayName("isBlank() 对纯空白字符返回 true")
        void isBlankWhitespace(String str) {
            assertThat(StringUtils.isBlank(str)).isTrue();
        }

        @Test
        @DisplayName("isBlank('hello') 返回 false")
        void isBlankNonBlank() {
            assertThat(StringUtils.isBlank("hello")).isFalse();
        }

        @Test
        @DisplayName("isNotBlank('hello') 返回 true")
        void isNotBlankNonBlank() {
            assertThat(StringUtils.isNotBlank("hello")).isTrue();
        }

        @Test
        @DisplayName("isNotBlank(' ') 返回 false")
        void isNotBlankWhitespace() {
            assertThat(StringUtils.isNotBlank(" ")).isFalse();
        }
    }

    @Nested
    @DisplayName("camelToUnderline 驼峰转下划线")
    class CamelToUnderlineTest {

        @ParameterizedTest
        @CsvSource({
                "userName, user_name",
                "createdAt, created_at",
                "userId, user_id",
                "httpResponse, http_response"
        })
        @DisplayName("驼峰命名字符串转换为下划线命名")
        void shouldConvertCamelToUnderline(String input, String expected) {
            assertThat(StringUtils.camelToUnderline(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("全小写字符串保持不变")
        void allLowerCaseUnchanged() {
            assertThat(StringUtils.camelToUnderline("name")).isEqualTo("name");
        }

        @Test
        @DisplayName("首字母大写时前面不加下划线")
        void firstCharUpperCaseNoLeadingUnderscore() {
            assertThat(StringUtils.camelToUnderline("UserName")).isEqualTo("user_name");
        }

        @Test
        @DisplayName("null 输入返回 null")
        void nullInputReturnsNull() {
            assertThat(StringUtils.camelToUnderline(null)).isNull();
        }

        @Test
        @DisplayName("空字符串输入返回空字符串")
        void emptyInputReturnsEmpty() {
            assertThat(StringUtils.camelToUnderline("")).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("underlineToCamel 下划线转驼峰")
    class UnderlineToCamelTest {

        @ParameterizedTest
        @CsvSource({
                "user_name, userName",
                "created_at, createdAt",
                "user_id, userId",
                "http_response, httpResponse"
        })
        @DisplayName("下划线命名字符串转换为驼峰命名")
        void shouldConvertUnderlineToCamel(String input, String expected) {
            assertThat(StringUtils.underlineToCamel(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("无下划线的字符串保持不变")
        void noUnderlineUnchanged() {
            assertThat(StringUtils.underlineToCamel("name")).isEqualTo("name");
        }

        @Test
        @DisplayName("连续下划线只影响下一个字符")
        void consecutiveUnderlines() {
            assertThat(StringUtils.underlineToCamel("a__b")).isEqualTo("aB");
        }

        @Test
        @DisplayName("null 输入返回 null")
        void nullInputReturnsNull() {
            assertThat(StringUtils.underlineToCamel(null)).isNull();
        }

        @Test
        @DisplayName("空字符串输入返回空字符串")
        void emptyInputReturnsEmpty() {
            assertThat(StringUtils.underlineToCamel("")).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("camelToUnderline + underlineToCamel 往返互转")
    class RoundTripTest {

        @Test
        @DisplayName("下划线 → 驼峰 → 下划线 往返结果一致")
        void underlineToCamelToUnderline() {
            String original = "user_created_at";
            String camel = StringUtils.underlineToCamel(original);
            String back = StringUtils.camelToUnderline(camel);

            assertThat(back).isEqualTo(original);
        }
    }
}
