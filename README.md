# Spring Boot Security 实现方案

## 项目概述

本项目实现了基于 Spring Boot Security 的用户认证和授权系统，包含以下功能：

-   用户注册和登录
-   基于 JWT 的 token 认证
-   角色基础权限控制
-   自定义认证过滤器
-   密码加密存储

## 实现步骤

1. 添加依赖配置

    - spring-boot-starter-security
    - spring-boot-starter-web
    - jjwt (JWT 支持)
    - spring-boot-starter-data-jpa

2. 创建基础实体类

    - User 实体
    - Role 实体
    - UserRole 关联

3. 实现核心安全配置

    - SecurityConfig 配置类
    - JwtTokenProvider 工具类
    - CustomUserDetailsService 实现

4. 创建认证控制器

    - AuthController 处理注册登录
    - UserController 测试权限控制

5. 实现 JWT 过滤器

    - JwtAuthenticationFilter
    - 集成到 Security 配置中

6. 数据库配置
    - application.yml 配置文件
    - 初始化 SQL 脚本
