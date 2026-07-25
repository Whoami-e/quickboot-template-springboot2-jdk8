package com.quickboot.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应封装。
 *
 * <p>封装分页查询结果，包含当前页数据列表及分页元信息（总记录数、当前页码、每页大小、总页数）。
 *
 * @param <T> 列表元素类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    /** 当前页数据列表 */
    private List<T> records;
    /** 总记录数 */
    private long total;
    /** 当前页码 */
    private long current;
    /** 每页大小 */
    private long size;
    /** 总页数 */
    private long pages;

    /**
     * 根据分页参数构建分页响应。
     *
     * <p>总页数计算逻辑：当 total 为 0 时总页数为 0；否则使用向上取整公式
     * {@code (total + size - 1) / size} 计算，即等价于 {@code Math.ceil(total / size)}。
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param total   总记录数
     * @param records 当前页数据列表
     * @param <T>     列表元素类型
     * @return 填充完整分页信息的响应对象
     */
    public static <T> PageResponse<T> of(long current, long size, long total, List<T> records) {
        long pages = total == 0 ? 0 : (total + size - 1) / size;
        return new PageResponse<>(records, total, current, size, pages);
    }
}
