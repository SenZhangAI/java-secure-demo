package com.example.security.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CsrfTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            response.setHeader("X-CSRF-TOKEN", csrf.getToken());

            // 设置 SameSite 属性
            String cookies = response.getHeader("Set-Cookie");
            if (cookies != null) {
                response.setHeader("Set-Cookie", cookies + "; SameSite=Strict");
            }
        }

        // 验证 Origin/Referer
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String serverName = request.getServerName();

        if ((origin != null && !origin.contains(serverName)) ||
                (referer != null && !referer.contains(serverName))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid origin");
            return;
        }

        filterChain.doFilter(request, response);
    }
}