package com.example.security.annotation;

import java.util.function.Function;

public enum SensitiveStrategy {
    /**
     * 用户名脱敏
     */
    USERNAME(s -> s.replaceAll("(?<=.{1}).(?=.{1})", "*")),

    /**
     * 手机号脱敏
     */
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

    /**
     * 邮箱脱敏
     */
    EMAIL(s -> {
        if (s.contains("@")) {
            String[] parts = s.split("@");
            String name = parts[0];
            return name.charAt(0) + "***" + name.charAt(name.length() - 1) + "@" + parts[1];
        }
        return s;
    }),

    /**
     * 身份证脱敏
     */
    ID_CARD(s -> s.replaceAll("(\\d{6})\\d{8}(\\w{4})", "$1********$2")),

    /**
     * 银行卡脱敏
     */
    BANK_CARD(s -> s.replaceAll("(\\d{4})\\d+(\\d{4})", "$1********$2")),

    /**
     * 地址脱敏
     */
    ADDRESS(s -> s.length() <= 8 ? s : s.substring(0, 8) + "****"),

    /**
     * 自定义脱敏规则
     */
    CUSTOM(s -> "******");

    private final Function<String, String> desensitizer;

    SensitiveStrategy(Function<String, String> desensitizer) {
        this.desensitizer = desensitizer;
    }

    public Function<String, String> getDesensitizer() {
        return desensitizer;
    }
}