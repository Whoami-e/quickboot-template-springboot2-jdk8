package com.quickboot.common.enums;

/**
 * 枚举序列化接口。
 *
 * <p>业务枚举实现此接口后，可通过统一的方式获取枚举的编码与描述，
 * 便于在 JSON 序列化、数据库存取等场景中统一处理。
 */
public interface BaseEnum {

    /**
     * 获取枚举编码。
     *
     * @return 枚举编码
     */
    Integer getCode();

    /**
     * 获取枚举描述信息。
     *
     * @return 枚举描述文本
     */
    String getDescription();
}
