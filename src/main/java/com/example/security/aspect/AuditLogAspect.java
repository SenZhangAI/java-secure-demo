package com.example.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class AuditLogAspect {
    private static final Logger logger = LoggerFactory.getLogger("AUDIT_LOG");

    @Around("@annotation(com.example.security.annotation.AuditLog)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        String username = getCurrentUsername();
        String ipAddress = getClientIp();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            // 记录请求信息
            logger.info("AUDIT|{}|{}|{}|START|参数: {}",
                    username, ipAddress, methodName,
                    maskSensitiveData(Arrays.toString(joinPoint.getArgs())));

            Object result = joinPoint.proceed();

            // 记录成功响应
            logger.info("AUDIT|{}|{}|{}|SUCCESS|耗时: {}ms",
                    username, ipAddress, methodName,
                    LocalDateTime.now().minusNanos(startTime.getNano()).getNano() / 1_000_000);

            return result;
        } catch (Exception e) {
            // 记录失败信息
            logger.error("AUDIT|{}|{}|{}|FAILED|异常: {}",
                    username, ipAddress, methodName, e.getMessage());
            throw e;
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }

    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String maskSensitiveData(String data) {
        // 对敏感数据进行脱敏
        return data.replaceAll("password=\\w+", "password=*****")
                .replaceAll("\\d{16,19}", "****") // 银行卡
                .replaceAll("\\d{3}-\\d{2}-\\d{4}", "***-**-****") // SSN
                .replaceAll("\\d{11}", "*****"); // 手机号
    }
}