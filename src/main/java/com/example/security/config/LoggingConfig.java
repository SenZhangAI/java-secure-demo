package com.example.security.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class LoggingConfig {

    @Bean
    public RollingFileAppender<ILoggingEvent> secureFileAppender() {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(new ch.qos.logback.classic.LoggerContext());

        // 设置日志文件路径
        appender.setFile("logs/audit.log");

        // 配置滚动策略
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(appender.getContext());
        rollingPolicy.setFileNamePattern("logs/audit-%d{yyyy-MM-dd}.log");
        rollingPolicy.setMaxHistory(30); // 保留30天的日志
        rollingPolicy.setParent(appender);
        rollingPolicy.start();

        appender.setRollingPolicy(rollingPolicy);

        // 设置日志事件处理
        appender.setEncoder(new ch.qos.logback.classic.encoder.PatternLayoutEncoder() {
            {
                setContext(appender.getContext());
                setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
                start();
            }
        });

        // 添加签名处理
        appender.addFilter(new Filter<ILoggingEvent>() {
            @Override
            public FilterReply decide(ILoggingEvent event) {
                try {
                    calculateSignature(event.getFormattedMessage());
                    return FilterReply.ACCEPT;
                } catch (Exception e) {
                    return FilterReply.DENY;
                }
            }
        });

        appender.start();
        return appender;
    }

    private String calculateSignature(String message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}