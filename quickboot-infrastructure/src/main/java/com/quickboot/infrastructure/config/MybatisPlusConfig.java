package com.quickboot.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 基础配置类。
 *
 * <p>本类为常规 {@code @Configuration}，无条件装配注解——只要 infrastructure 模块
 * 在 classpath 上（mybatis-plus-boot-starter 为非 optional 依赖），本配置始终生效。</p>
 *
 * <p>职责：</p>
 * <ul>
 *   <li>通过 {@link MapperScan} 指定 Mapper 接口扫描包路径
 *       {@code com.quickboot.infrastructure.mapper}，使 MyBatis-Plus 能自动注册 Mapper 代理。</li>
 *   <li>注册分页拦截器，为所有查询自动追加 {@code LIMIT}/{@code OFFSET} 与 {@code COUNT} 语句。</li>
 * </ul>
 */
@Configuration
@MapperScan("com.quickboot.infrastructure.mapper") // 扫描 infrastructure 层的 Mapper 接口
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 分页插件（PaginationInnerInterceptor）。
     *
     * <p>分页拦截器的作用：在执行 SQL 前，拦截 MyBatis 的查询请求，
     * 自动为原始 SQL 追加 {@code LIMIT} 和 {@code OFFSET} 子句实现物理分页，
     * 同时生成对应的 {@code COUNT} 查询以返回总记录数，避免手动拼接分页 SQL。</p>
     *
     * <p>{@link DbType#MYSQL} 指定数据库方言为 MySQL，确保生成的分页 SQL 语法
     * 与 MySQL 兼容（如使用 {@code LIMIT offset, size} 语法）。若切换数据库，
     * 需将此参数改为对应的 {@link DbType}（如 {@code DbType.POSTGRE_SQL}）。</p>
     *
     * @return MybatisPlusInterceptor 已装配分页内置拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页内置拦截器，指定 MySQL 方言
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
