# JWT身份验证与授权机制说明

## 系统概述

系统采用基于JWT(JSON Web Token)的认证授权体系，结合Spring Security框架实现，同时整合OAuth2认证，构建了完整的安全架构。

## JWT认证流程

1. 用户通过`/api/auth/login`或OAuth2登录端点进行身份验证
2. 认证成功后，系统生成JWT令牌返回给客户端
3. 客户端后续请求在Authorization头中携带JWT令牌
4. `JwtAuthenticationFilter`拦截请求，验证令牌有效性
5. 验证通过后，将用户身份信息加载到SecurityContext

## 权限控制机制

系统实现两级权限控制：

1. **基于角色的静态权限**：通过配置指定路径的访问角色要求
    - 普通用户(USER)：可访问图片查询API
    - 管理员(ADMIN)：可访问图片上传API

2. **动态权限控制**：通过`DynamicSecurityMetadataSource`和`DynamicAccessDecisionManager`实现
    - 支持运行时调整API访问权限策略
    - 细粒度的权限控制，可基于URL、方法、参数等因素

## 安全配置详解

1. **无状态会话**：采用`SessionCreationPolicy.STATELESS`配置
2. **密码加密**：使用BCryptPasswordEncoder进行密码强哈希(12轮迭代)
3. **CSRF防护**：API端点和OAuth2回调豁免CSRF检查
4. **跨域资源共享**：配置CORS支持localhost:3000源
5. **认证入口点**：自定义错误响应，区分不同认证失败原因

## OAuth2整合

系统已整合OAuth2认证机制：
- 支持标准OAuth2登录流程
- 通过外部认证提供商(如Google、GitHub等)进行身份验证
- 与JWT认证机制共同工作，确保统一的权限控制

## API安全路径配置

| 路径 | 访问权限 |
|------|---------|
| `/api/auth/**` | 允许匿名访问 |
| `/api/images/**` (GET) | 需要USER或ADMIN角色 |
| `/api/images/**` (POST) | 需要ADMIN角色 |
| 其他API路径 | 需要认证并根据动态权限判定 |
| Swagger文档相关路径 | 允许匿名访问 |