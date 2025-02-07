package com.example.security.listener;

import org.springframework.stereotype.Component;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.info("新会话已创建: {}", se.getSession().getId());
        se.getSession().setMaxInactiveInterval(1800); // 30分钟超时
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.info("会话已销毁: {}", se.getSession().getId());
    }
}