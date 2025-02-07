package com.example.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.security.repository")
@EnableTransactionManagement
public class JpaConfig {
    // JPA 已经默认使用 PreparedStatement，这里只需要确保所有查询都使用参数化方式
}