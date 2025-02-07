package com.example.security.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request);

        // 设置安全响应头
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline';");

        filterChain.doFilter(xssRequest, response);
    }
}