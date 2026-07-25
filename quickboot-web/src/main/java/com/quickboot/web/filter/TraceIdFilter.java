package com.quickboot.web.filter;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 链路追踪过滤器：为每个请求生成/透传 traceId，写入 MDC 供日志输出，并回写到响应头。
 * <p>
 * 继承 {@link OncePerRequestFilter} 确保每个请求只执行一次（避免转发/包含导致的重复执行）。
 * <p>
 * 条件装配：受 {@code quickboot.trace.enabled} 控制，默认（缺省）开启。
 * <p>
 * 优先从请求头 {@code X-Trace-Id} 获取（支持上游服务透传 traceId，串联整条调用链），
 * 否则生成 32 位无横线 UUID。
 */
@Component
// 最高优先级：确保 traceId 在其它过滤器/拦截器执行前就已写入 MDC，
// 否则后续过滤器产生的日志无法关联到本次请求的 traceId，影响链路追踪
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "quickboot.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 提取或生成 traceId，写入 MDC 后放行请求，并在结束时清理 MDC。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 处理异常
     * @throws IOException IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 优先从请求头获取，支持上游服务透传 traceId 串联整条调用链
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            // 上游未携带则自行生成 32 位无横线 UUID，保证全局唯一
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        // 写入 MDC，logback 日志 pattern 中的 %X{traceId} 占位符即从此处取值
        MDC.put(TRACE_ID, traceId);
        // 回写到响应头，便于前端/下游关联同一次请求
        response.setHeader(TRACE_ID_HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 必须清理 MDC：Tomcat 使用线程池复用线程，若不清理会导致上一个请求的 traceId 残留（ThreadLocal 泄漏），
            // 后续复用该线程的请求日志会打印错误的 traceId，干扰链路追踪
            MDC.clear();
        }
    }
}
