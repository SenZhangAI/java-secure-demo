package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@Configuration
public class SqlInjectionConfig {

    private static final String SQL_INJECTION_PATTERN = "(?i).*(\\b(select|insert|update|delete|drop|union|exec|execute)\\b).*";

    /**
     * 在 Spring Boot 应用程序中，SqlInjectionConfig 中定义的 sqlInjectionFilter 是通过 Spring
     * 的自动配置机制被使用的。
     * 由于该类使用了 @Configuration 注解，并且 sqlInjectionFilter 方法使用了 @Bean 注解，Spring
     * 会自动将这个过滤器注册到 Servlet 过滤器链中。
     * 具体工作流程如下：
     * 当应用启动时，Spring 会扫描带有 @Configuration 注解的类
     * 发现 @Bean 注解的 sqlInjectionFilter 方法
     * Spring 会自动创建这个过滤器的实例
     * 由于这个过滤器继承自 OncePerRequestFilter，它会被自动添加到 Spring Security 的过滤器链中
     * 这意味着所有进入应用的 HTTP 请求都会经过这个过滤器的处理。每当有请求进来时：
     * 过滤器会检查请求参数和 URI 是否包含可疑的 SQL 注入模式
     * 如果检测到可疑内容，请求会被拦截并返回 400 错误
     * 如果请求正常，则会继续传递给下一个过滤器或处理器
     * 你不需要在代码中显式地调用这个过滤器，Spring Boot 会自动处理这些配置。这是 Spring Boot 的约定大于配置原则的一个很好的例子。
     * 
     * @return
     */
    @Bean
    public OncePerRequestFilter sqlInjectionFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException, IOException {

                if (containsSqlInjection(request)) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "检测到潜在的 SQL 注入攻击");
                    return;
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private boolean containsSqlInjection(HttpServletRequest request) {
        Pattern pattern = Pattern.compile(SQL_INJECTION_PATTERN);

        // 检查请求参数
        for (String[] values : request.getParameterMap().values()) {
            for (String value : values) {
                if (value != null && pattern.matcher(value).matches()) {
                    return true;
                }
            }
        }

        // 检查请求路径
        String requestURI = request.getRequestURI();
        if (pattern.matcher(requestURI).matches()) {
            return true;
        }

        return false;
    }
}