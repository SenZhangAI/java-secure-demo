package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public HeaderWriter securityHeadersWriter() {
        return new StaticHeadersWriter(
                // 防止网站被嵌入到iframe中，防止点击劫持
                "X-Frame-Options", "DENY",
                // 防止浏览器对响应的MIME类型进行猜测
                "X-Content-Type-Options", "nosniff",
                // 启用浏览器XSS过滤
                "X-XSS-Protection", "1; mode=block",
                // 严格的传输安全
                "Strict-Transport-Security", "max-age=31536000; includeSubDomains",
                // 引用策略
                "Referrer-Policy", "strict-origin-when-cross-origin",
                // 内容安全策略
                "Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "font-src 'self'; " +
                        "frame-ancestors 'none';");
    }

    @Bean
    public HeaderWriter apiHeadersWriter() {
        return new DelegatingRequestMatcherHeaderWriter(
                new AntPathRequestMatcher("/api/**"),
                new StaticHeadersWriter("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate"));
    }
}