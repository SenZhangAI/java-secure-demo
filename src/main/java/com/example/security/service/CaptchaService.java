package com.example.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    private final LoadingCache<String, String> captchaCache;

    public CaptchaService() {
        captchaCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        return "";
                    }
                });
    }

    public void saveCaptcha(String sessionId, String code) {
        captchaCache.put(sessionId, code);
    }

    public boolean validateCaptcha(String sessionId, String code) {
        try {
            String savedCode = captchaCache.get(sessionId);
            return savedCode.equalsIgnoreCase(code);
        } catch (Exception e) {
            return false;
        } finally {
            captchaCache.invalidate(sessionId);
        }
    }
}