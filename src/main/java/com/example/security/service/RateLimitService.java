package com.example.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final LoadingCache<String, RateLimiter> ipRateLimiters;
    private static final int MAX_REQUESTS_PER_SECOND = 10;

    public RateLimitService() {
        ipRateLimiters = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<String, RateLimiter>() {
                    @Override
                    public RateLimiter load(String key) {
                        return RateLimiter.create(MAX_REQUESTS_PER_SECOND);
                    }
                });
    }

    public boolean tryAccess(String ip) {
        try {
            RateLimiter rateLimiter = ipRateLimiters.get(ip);
            return rateLimiter.tryAcquire();
        } catch (Exception e) {
            return false;
        }
    }
}