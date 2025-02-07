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

## 测试用例

1. 认证测试

    - 用户注册测试
    - 用户登录测试
    - Token 生成测试

2. 权限测试
    - 公共接口测试
    - 用户接口测试
    - 管理员接口测试

# Spring Security Demo

基于 Spring Boot 和 JWT 的认证授权系统示例。

## 功能特性

-   用户注册和登录
-   JWT token 认证
-   基于角色的权限控制
-   Swagger API 文档
-   全局异常处理

## 技术栈

-   Spring Boot 2.7.0
-   Spring Security
-   MySQL 8.0
-   JWT
-   Swagger/OpenAPI

## 快速开始

### 环境要求

-   JDK 8+
-   Maven 3.6+
-   Docker & Docker Compose

### 启动步骤

1. 启动 MySQL 数据库

```bash
docker-compose up -d
```

2. 等待 MySQL 完全启动（约 30 秒），可以通过以下命令查看状态：

```bash
docker-compose ps
```

3. 数据库初始化

-   系统会自动创建数据表（JPA auto-ddl）
-   初始角色数据会通过 data.sql 自动导入

4. 启动应用

```bash
mvn spring-boot:run
```

### API 文档

启动应用后，访问 Swagger 文档：

```
http://localhost:8080/swagger-ui.html
```

### 测试接口

1. 注册新用户

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

2. 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

3. 获取用户信息（需要 token）

```bash
curl -X GET http://localhost:8080/api/auth/user/info \
  -H "Authorization: Bearer {your_token}"
```

## 开发说明

### 数据库配置

数据库连接信息在 application.yml 中配置：

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3307/security_demo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
        username: root
        password: root
```

### 运行测试(依然未跑通，暂时跳过)

```bash
mvn test
```

## 安全策略优化路线图

### 已完成

-   [x] 基础认证授权功能
-   [x] JWT token 实现
-   [x] SQL 注入防护
    -   [x] 使用 PreparedStatement（通过 JPA 默认支持）
    -   [x] SQL 注入过滤器
    -   [x] 参数化查询配置
-   [x] XSS 攻击防护
    -   [x] 输入数据转义
    -   [x] XSS 过滤器实现
    -   [x] 设置安全响应头（CSP）
-   [x] CSRF 攻击防护
    -   [x] 实现 CSRF token
    -   [x] SameSite Cookie 设置
    -   [x] 验证 Origin/Referer

### 待实现

1. 密码安全

    - 密码强度校验
    - 密码加密存储优化
    - 密码重试次数限制
    - 定期密码更新策略

2. 会话安全

    - Session 固定攻击防护
    - Session 超时处理
    - 并发会话控制

3. 请求限流

    - 实现 IP 限流
    - API 访问频率限制
    - 验证码机制

4. 敏感数据保护

    - 数据脱敏
    - 传输加密
    - 敏感信息加密存储

5. 日志安全

    - 安全审计日志
    - 日志脱敏
    - 日志防篡改

6. 文件上传安全

    - 文件类型验证
    - 文件大小限制
    - 文件存储安全

7. 其他安全措施
    - HTTP 安全头配置
    - 错误信息处理
    - 依赖包安全检查
    - 定期安全扫描

每个里程碑完成后，将在此更新进度。
