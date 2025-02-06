# Spring Boot Security 实现方案

## 项目概述

本项目实现了基于 Spring Boot Security 的用户认证和授权系统，包含以下功能：

-   用户注册和登录
-   基于 JWT 的 token 认证
-   角色基础权限控制
-   自定义认证过滤器
-   密码加密存储

## 实现步骤

1. 添加依赖配置 ✅

    - spring-boot-starter-security
    - spring-boot-starter-web
    - jjwt (JWT 支持)
    - spring-boot-starter-data-jpa

2. 创建基础实体类 ✅

    - User 实体
    - Role 实体
    - UserRole 关联

3. 实现核心安全配置 ✅

    - SecurityConfig 配置类
    - JwtTokenProvider 工具类
    - CustomUserDetailsService 实现

4. 创建认证控制器 🚧

    - AuthController 处理注册登录 ✅
    - UserController 测试权限控制 ✅

5. 实现 JWT 过滤器 ✅

    - JwtAuthenticationFilter
    - 集成到 Security 配置中

6. 数据库配置 ✅
    - application.yml 配置文件
    - 初始化 SQL 脚本

## 下一步工作

1. 创建认证控制器（AuthController）

    - 实现用户注册接口
    - 实现用户登录接口
    - 实现获取用户信息接口

2. 创建用户控制器（UserController）

    - 实现受保护的 API 接口
    - 添加角色权限控制
    - 测试用例编写

3. 补充异常处理 ✅

    - 创建全局异常处理器
    - 自定义业务异常
    - 统一响应格式

4. 添加接口文档
    - 集成 Swagger ✅
    - API 文档编写 ✅
    - 测试用例说明 ✅

## 测试用例

1. 认证测试

    - 用户注册测试
    - 用户登录测试
    - Token 生成测试

2. 权限测试
    - 公共接口测试
    - 用户接口测试
    - 管理员接口测试

## 快速开始

1. 环境要求

    - JDK 8+
    - Maven 3.6+
    - MySQL 5.7+

2. 数据库配置

    - 创建数据库：security_demo
    - 更新 application.yml 中的数据库配置

3. 运行项目

    ```bash
    mvn spring-boot:run
    ```

4. 测试接口
    - 访问 Swagger 文档：http://localhost:8080/swagger-ui/
    - 默认测试账号：
        - 普通用户：user/password
        - 管理员：admin/password

## 主要功能

1. 认证功能

    - 基于 JWT 的 Token 认证
    - 用户名密码登录
    - 注册新用户

2. 授权功能

    - 基于角色的权限控制
    - URL 级别的访问控制
    - 方法级别的安全注解

3. 安全特性
    - 密码加密存储
    - Token 过期控制
    - 全局异常处理
