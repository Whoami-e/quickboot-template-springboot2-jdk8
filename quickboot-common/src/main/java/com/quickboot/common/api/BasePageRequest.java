package com.quickboot.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页请求基类。
 *
 * <p>封装分页查询所需的页码与每页大小参数，并提供向 MyBatis-Plus {@link Page} 转换的便捷方法。
 * 各分页查询请求 DTO 可继承此类以复用分页参数及校验逻辑。
 *
 * @param <T> 查询条件类型
 */
@Data
public class BasePageRequest<T> {

    /** 当前页码，最小为 1 */
    @Min(1)
    private Integer current = 1;

    /** 每页大小，最小为 1；@Max(100) 限制上限以防止一次查询过多数据导致性能问题 */
    @Min(1)
    @Max(100)
    private Integer size = 10;

    /**
     * 将分页参数转换为 MyBatis-Plus 的 {@link Page} 对象。
     *
     * @return 包含当前页码和每页大小的 Page 对象
     */
    public Page<T> toPage() {
        return new Page<>(current, size);
    }
}
