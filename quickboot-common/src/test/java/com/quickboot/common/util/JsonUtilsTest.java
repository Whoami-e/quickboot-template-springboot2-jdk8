package com.quickboot.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link JsonUtils} 单元测试。
 *
 * <p>验证 JSON 序列化与反序列化操作：
 * {@link JsonUtils#toJson(Object)}、{@link JsonUtils#fromJson(String, Class)}、
 * {@link JsonUtils#fromJson(String, TypeReference)} 以及 {@code toJson(null)} 的行为。
 */
@DisplayName("JsonUtils JSON 工具类测试")
class JsonUtilsTest {

    @Test
    @DisplayName("toJson() 正确序列化对象")
    void toJsonShouldSerializeObject() {
        Map<String, Object> obj = new java.util.LinkedHashMap<>();
        obj.put("name", "quickboot");
        obj.put("version", 1);

        String json = JsonUtils.toJson(obj);

        assertThat(json).isEqualTo("{\"name\":\"quickboot\",\"version\":1}");
    }

    @Test
    @DisplayName("toJson() 序列化简单 POJO")
    void toJsonShouldSerializePojo() {
        Person person = new Person("alice", 30);

        String json = JsonUtils.toJson(person);

        assertThat(json).contains("\"name\":\"alice\"");
        assertThat(json).contains("\"age\":30");
    }

    @Test
    @DisplayName("toJson(null) 返回 'null' 字符串")
    void toJsonNullReturnsNullString() {
        String json = JsonUtils.toJson(null);

        assertThat(json).isEqualTo("null");
    }

    @Test
    @DisplayName("fromJson() 正确反序列化 JSON 字符串")
    void fromJsonShouldDeserializeString() {
        String json = "{\"name\":\"alice\",\"age\":30}";

        Person person = JsonUtils.fromJson(json, Person.class);

        assertThat(person.getName()).isEqualTo("alice");
        assertThat(person.getAge()).isEqualTo(30);
    }

    @Test
    @DisplayName("fromJson() 带 TypeReference 正确反序列化泛型 List")
    void fromJsonWithTypeReferenceShouldDeserializeGenericList() {
        String json = "[\"apple\",\"banana\",\"cherry\"]";

        List<String> fruits = JsonUtils.fromJson(json, new TypeReference<List<String>>() {});

        assertThat(fruits).hasSize(3);
        assertThat(fruits).containsExactly("apple", "banana", "cherry");
    }

    @Test
    @DisplayName("fromJson() 带 TypeReference 正确反序列化泛型 Map")
    void fromJsonWithTypeReferenceShouldDeserializeGenericMap() {
        String json = "{\"key\":\"value\",\"num\":42}";

        Map<String, Object> map = JsonUtils.fromJson(json, new TypeReference<Map<String, Object>>() {});

        assertThat(map).hasSize(2);
        assertThat(map.get("key")).isEqualTo("value");
        assertThat(map.get("num")).isEqualTo(42);
    }

    @Test
    @DisplayName("toJson + fromJson 往返操作保持数据一致")
    void roundTripShouldPreserveData() {
        List<Person> people = Arrays.asList(
                new Person("alice", 30),
                new Person("bob", 25)
        );

        String json = JsonUtils.toJson(people);
        List<Person> result = JsonUtils.fromJson(json, new TypeReference<List<Person>>() {});

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("alice");
        assertThat(result.get(1).getAge()).isEqualTo(25);
    }

    @Test
    @DisplayName("fromJson() 遇到非法 JSON 抛出 RuntimeException")
    void fromJsonInvalidJsonThrowsRuntimeException() {
        assertThatThrownBy(() -> JsonUtils.fromJson("{invalid}", Person.class))
                .isInstanceOf(RuntimeException.class);
    }

    /** 测试用简单 POJO。 */
    public static class Person {
        private String name;
        private int age;

        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
