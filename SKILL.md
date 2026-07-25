# QuickBoot Spring Boot 2 Scaffold 项目了解指南

## 项目概述

QuickBoot 是一个基于 **Spring Boot 2.7.18 + JDK 8** 的多模块 Maven 脚手架项目，采用 **DDD（领域驱动设计）分层架构**。项目内置 CORS 跨域、MDC 链路追踪、全局异常处理、统一 JSON 序列化等基础设施能力，并通过**特性开关 + 可插拔依赖**实现 Redis 缓存与 Sa-Token 鉴权的按需启用。当前实例化后包名为 `com.quickboot`，Maven GroupId 为 `com.quickboot`，服务端口 `8080`。

## 技术栈

| 技术 | 版本 | 用途 | 备注 |
|------|------|------|------|
| Spring Boot | 2.7.18 | 应用框架 | 父 POM |
| JDK | 1.8 | Java 运行环境 | |
| MyBatis-Plus | 3.5.5 | ORM 框架 + 分页拦截器 | infrastructure 模块 |
| mybatis-plus-generator | 3.5.5 | 代码生成器 | 仅 test scope |
| velocity-engine-core | 2.3 | 代码生成模板引擎 | 仅 test scope |
| Sa-Token | 1.37.0 | 鉴权框架 | **可插拔**，optional |
| spring-boot-starter-data-redis | (随 Spring Boot) | Redis 缓存 | **可插拔**，optional |
| spring-boot-starter-actuator | (随 Spring Boot) | 监控端点 | web 模块 |
| spring-boot-starter-web | (随 Spring Boot) | Web MVC | web 模块 |
| spring-boot-starter-validation | (随 Spring Boot) | 参数校验 | web 模块 |
| jackson-databind | (随 Spring Boot) | JSON 序列化 | common 模块，optional |
| validation-api | (随 Spring Boot) | 参数校验注解 | common 模块，optional |
| Lombok | 1.18.32 | 代码简化 | 全模块 |
| Logback | (随 Spring Boot) | 日志框架 | 含 MDC traceId |

## 模块架构

项目采用 **5 模块 DDD 分层架构**，依赖方向为自上而下：

```
quickboot-web          ← HTTP 入口层（Controller、Filter、Config、全局异常处理、启动类）
    ↓
quickboot-application  ← 应用服务层（编排业务逻辑）
    ↓
quickboot-infrastructure ← 基础设施层（MyBatis-Plus / Redis / Sa-Token 配置）
    ↓
quickboot-domain       ← 领域层（领域模型，纯 POJO）
    ↓
quickboot-common       ← 公共层（API 封装、异常、枚举、工具类）
```

### 各模块详细说明

#### 1. quickboot-common（公共层，11 个类）

- **包路径**: `com.quickboot.common`
- **职责**: 提供跨模块复用的通用类——统一 API 响应封装、错误码、异常、枚举接口、工具类
- **依赖**: Lombok, jackson-databind (optional), validation-api (optional), mybatis-plus-extension (optional), spring-boot-starter-test (test)
- **类清单**:

| 包 | 类名 | 职责 |
|----|------|------|
| `api` | `ApiResponse<T>` | 统一 API 响应封装，字段：`code`、`message`、`data`。工厂方法：`success(data)`、`success()`、`fail(code, message)` |
| `api` | `ErrorCode` | 错误码枚举，实现 `IErrorCode` + `BaseEnum`。含 `SUCCESS(0)`、`BAD_REQUEST(400)`、`UNAUTHORIZED(401)`、`FORBIDDEN(403)`、`NOT_FOUND(404)`、`TOO_MANY_REQUESTS(429)`、`INTERNAL_ERROR(500)`、`SERVICE_UNAVAILABLE(503)` |
| `api` | `IErrorCode` | 错误码接口，定义 `getCode()` 和 `getMessage()` |
| `api` | `PageResponse<T>` | 分页响应封装，含 `records`、`total`、`current`、`size`、`pages`。工厂方法 `of(current, size, total, records)` 自动计算总页数 |
| `api` | `BasePageRequest<T>` | 分页请求基类，含 `current`(默认1) 和 `size`(默认10)，带 `@Min`/`@Max` 校验，提供 `toPage()` 转换为 MyBatis-Plus `Page` |
| `enums` | `BaseEnum` | 枚举序列化接口，定义 `getCode()` 和 `getDescription()` |
| `exception` | `BusinessException` | 业务异常（继承 `RuntimeException`），携带 `code`。支持 `(message)`、`(code, message)`、`(IErrorCode)` 三种构造方式 |
| `util` | `JsonUtils` | JSON 工具类（基于 Jackson `ObjectMapper`），提供 `toJson()`、`fromJson(Class)`、`fromJson(TypeReference)` |
| `util` | `DateUtils` | 日期工具类（基于 `LocalDateTime`），提供 `format()`、`parse()`、`now()` |
| `util` | `StringUtils` | 字符串工具类，提供 `isEmpty`/`isNotEmpty`/`isBlank`/`isNotBlank`、`camelToUnderline`/`underlineToCamel` |
| `util` | `CollectionUtils` | 集合工具类，提供 `isEmpty`/`isNotEmpty`、`emptyList()` |

- **测试类**: `ApiResponseTest`、`ErrorCodeTest`、`BusinessExceptionTest`、`DateUtilsTest`、`JsonUtilsTest`、`StringUtilsTest`（JUnit 5 + AssertJ）

#### 2. quickboot-domain（领域层，1 个类）

- **包路径**: `com.quickboot.domain.model`
- **职责**: 领域模型定义，纯 POJO，不含 Spring 注解
- **依赖**: quickboot-common, Lombok
- **类清单**:

| 类名 | 职责 |
|------|------|
| `HealthStatus` | 健康检查模型，字段：`status`、`application`、`checkedAt`(LocalDateTime) |

- **约定**: 新增实体类应放在 `model` 子包中

#### 3. quickboot-infrastructure（基础设施层，3 个类）

- **包路径**: `com.quickboot.infrastructure.config`
- **职责**: 数据访问、缓存、鉴权等中间件配置
- **依赖**: quickboot-domain, mybatis-plus-boot-starter, spring-boot-starter-data-redis (optional), sa-token-spring-boot-starter (optional), Lombok
- **类清单**:

| 类名 | 职责 | 条件装配 |
|------|------|----------|
| `MybatisPlusConfig` | MyBatis-Plus 可插拔配置：`@MapperScan("com.quickboot.infrastructure.mapper")` + 分页拦截器 `PaginationInnerInterceptor`(MySQL 方言) | `@ConditionalOnClass(name="...SqlSessionFactory")` + `@ConditionalOnProperty("quickboot.mybatis-plus.enabled=true", matchIfMissing=false)` |
| `RedisConfig` | Redis 缓存配置：自定义 `RedisTemplate`(key=String, value=JSON)、`StringRedisTemplate`，`@EnableCaching` | `@ConditionalOnClass("RedisOperations")` + `@ConditionalOnProperty("quickboot.redis.enabled=true")` |
| `SaTokenConfig` | Sa-Token 鉴权配置：注册 `SaInterceptor`，拦截 `/api/**`，放行 `/health`、`/actuator/**` | `@ConditionalOnClass("StpUtil")` + `@ConditionalOnProperty("quickboot.sa-token.enabled=true")` |

- **约定**: Mapper 接口放在 `com.quickboot.infrastructure.mapper` 包下

#### 4. quickboot-application（应用服务层，1 个类）

- **包路径**: `com.quickboot.application.service`
- **职责**: 业务编排，协调领域层和基础设施层
- **依赖**: quickboot-common, quickboot-domain, quickboot-infrastructure, spring-context, Lombok
- **类清单**:

| 类名 | 职责 |
|------|------|
| `HealthService` | 健康检查服务（`@Service`），返回 `HealthStatus` |

#### 5. quickboot-web（Web 入口层，7 个主类 + 4 个测试类）

- **包路径**: `com.quickboot.web`
- **职责**: HTTP 接口暴露、过滤器、全局异常处理、启动入口
- **依赖**: quickboot-application, quickboot-common, spring-boot-starter-web, spring-boot-starter-validation, spring-boot-starter-actuator, sa-token-spring-boot-starter (optional), Lombok, spring-boot-starter-test (test), mybatis-plus-generator (test), velocity-engine-core (test)
- **主类清单**:

| 包 | 类名 | 职责 |
|----|------|------|
| (根) | `QuickbootApplication` | Spring Boot 启动类，`scanBasePackages = "com.quickboot"`，排除 `DataSourceAutoConfiguration`、`MybatisPlusAutoConfiguration`、`RedisAutoConfiguration`、`RedisRepositoriesAutoConfiguration` |
| `config` | `CorsConfig` | 跨域 CORS 配置（实现 `WebMvcConfigurer`），支持 `allowedOrigins`/`allowedMethods` 配置。`"*"` 时自动改用 `allowedOriginPatterns` 兼容凭证模式 | 
| `config` | `JacksonConfig` | 全局 Jackson 序列化配置（`Jackson2ObjectMapperBuilderCustomizer`）：禁用时间戳、统一日期格式、Long→String、忽略 null、时区 Asia/Shanghai、忽略未知字段 |
| `filter` | `TraceIdFilter` | MDC 链路追踪过滤器（`OncePerRequestFilter`），优先从 `X-Trace-Id` 请求头获取，否则生成 32 位 UUID，写入 MDC 并回写响应头 |
| `controller` | `HealthController` | `@RestController` + `@Validated`，`GET /health` 端点返回 `ApiResponse<HealthStatus>` |
| `handler` | `GlobalExceptionHandler` | `@RestControllerAdvice`，处理 `BusinessException`、参数校验异常、`NoHandlerFoundException`(404)、`HttpRequestMethodNotSupportedException`(400)、兜底 `Exception`(500) |
| `handler` | `SaTokenExceptionHandler` | Sa-Token 异常处理（`@Order(HIGHEST_PRECEDENCE)`），处理 `NotLoginException`(401)、`NotPermissionException`(403)、`NotRoleException`(403) |

- **测试类**:

| 类名 | 类型 | 说明 |
|------|------|------|
| `HealthControllerTest` | `@WebMvcTest` 切片测试 | MockMvc 验证 `/health` 端点响应结构，Mock `HealthService` |
| `QuickbootContextTest` | `@SpringBootTest` 全量测试 | 验证完整 Spring 上下文加载（回归闸门），`@ActiveProfiles("test")` |
| `GlobalExceptionHandlerTest` | 纯单元测试 | 直接实例化 handler 验证各异常处理逻辑（AssertJ） |
| `CodeGenerator` | 代码生成器（非测试） | MyBatis-Plus `FastAutoGenerator`，生成 Entity/Mapper/Service/Controller，位于 test 目录不参与打包 |

- **打包**: spring-boot-maven-plugin 配置在此模块

## 特性开关一览

所有特性开关统一在 `application.yml` 的 `quickboot` 前缀下配置：

| 开关 | 配置键 | 默认值 | 控制目标 | 装配机制 |
|------|--------|--------|----------|----------|
| CORS 跨域 | `quickboot.cors.enabled` | `true` | `CorsConfig` | `@ConditionalOnProperty(matchIfMissing=true)` |
| 链路追踪 | `quickboot.trace.enabled` | `true` | `TraceIdFilter` | `@ConditionalOnProperty(matchIfMissing=true)` |
| Redis 缓存 | `quickboot.redis.enabled` | `false` | `RedisConfig` | `@ConditionalOnClass` + `@ConditionalOnProperty` |
| Sa-Token 鉴权 | `quickboot.sa-token.enabled` | `false` | `SaTokenConfig` + `SaTokenExceptionHandler` | `@ConditionalOnClass` + `@ConditionalOnProperty` |
| MyBatis-Plus ORM | `quickboot.mybatis-plus.enabled` | `false` | `MybatisPlusConfig`（分页拦截器 + MapperScan） | `@ConditionalOnClass` + `@ConditionalOnProperty` + 主类排除 `MybatisPlusAutoConfiguration` |

CORS 和 TraceId 默认开启（`matchIfMissing=true`），Redis、Sa-Token 和 MyBatis-Plus 默认关闭，需显式设为 `true` 且满足 classpath 条件后才会装配。

## 可插拔功能说明

项目采用 **optional 依赖 + 条件装配** 的双重保护机制实现可插拔架构：

### Redis 可插拔

1. **依赖层**：infrastructure 模块将 `spring-boot-starter-data-redis` 声明为 `<optional>true</optional>`，web 层需显式引入才会传递到运行时 classpath
2. **装配层**：`RedisConfig` 使用 `@ConditionalOnClass(name="...RedisOperations")` + `@ConditionalOnProperty("quickboot.redis.enabled=true")`，classpath 不存在 Redis 类时跳过加载，不会触发 `NoClassDefFoundError`
3. **启动类**：`QuickbootApplication` 排除 `RedisAutoConfiguration` 和 `RedisRepositoriesAutoConfiguration`，避免无 Redis 环境启动失败
4. **设计细节**：`RedisConnectionFactory` 通过 `@Bean` 方法参数注入而非类字段声明，避免类加载期触发类型解析

**启用方式**：web 模块 pom 引入 `spring-boot-starter-data-redis`（去掉 optional 或显式声明）+ 配置 `quickboot.redis.enabled=true` + 配置 `spring.redis.host` 等连接信息

### Sa-Token 可插拔

1. **依赖层**：infrastructure 和 web 模块均将 `sa-token-spring-boot-starter` 声明为 `<optional>true</optional>`
2. **装配层**：`SaTokenConfig` 和 `SaTokenExceptionHandler` 均使用 `@ConditionalOnClass(name="...StpUtil")` + `@ConditionalOnProperty("quickboot.sa-token.enabled=true")`
3. **异常处理优先级**：`SaTokenExceptionHandler` 使用 `@Order(HIGHEST_PRECEDENCE)`，确保鉴权异常优先于 `GlobalExceptionHandler` 的兜底处理

**启用方式**：web 模块 pom 显式引入 `sa-token-spring-boot-starter`（去掉 optional）+ 配置 `quickboot.sa-token.enabled=true`

### MyBatis-Plus 可插拔

1. **条件装配层**：`MybatisPlusConfig` 使用 `@ConditionalOnClass(name="org.apache.ibatis.session.SqlSessionFactory")` + `@ConditionalOnProperty(prefix="quickboot.mybatis-plus", name="enabled", havingValue="true", matchIfMissing=false)` 双重条件守卫，仅当 classpath 存在 MyBatis 核心类且配置开关显式打开时才装配
2. **主类排除**：`QuickbootApplication` 的 `@SpringBootApplication(exclude=...)` 中排除 `MybatisPlusAutoConfiguration`，避免未启用时 MyBatis-Plus 自动扫描 DataSource 导致初始化失败
3. **服务层回退**：`UserService` 通过 `@Autowired(required=false)` 注入 `UserMapper`，当 Mapper 不可用时自动回退到内存 `ConcurrentHashMap` 模拟 CRUD，保证无数据库环境下服务仍可正常启动和演示
4. **注意**：`mybatis-plus-boot-starter` 在 infrastructure 模块中**未声明为 optional**（与 Redis/Sa-Token 不同），因此 MyBatis-Plus 核心类始终存在于 classpath，`@ConditionalOnClass` 条件始终满足，实际控制装配的是 `@ConditionalOnProperty` 属性开关和主类排除

**启用方式**：配置 `quickboot.mybatis-plus.enabled=true` + 添加 `spring.datasource` 数据源配置

## 配置文件

### 多环境配置

| 文件 | Profile | 特征 |
|------|---------|------|
| `application.yml` | (公共) | 端口 8080、默认 profile=dev、特性开关、Actuator 配置、Jackson 配置 |
| `application-dev.yml` | dev | 懒加载 `lazy-initialization=true`、业务包日志 DEBUG |
| `application-test.yml` | test | 业务包 INFO、框架包 WARN |
| `application-prod.yml` | prod | 业务包 INFO、框架包 WARN（收敛日志量）、MyBatis-Plus 开关显式声明 |

通过 `spring.profiles.active` 切换环境，或通过环境变量 `SPRING_PROFILES_ACTIVE` 覆盖。

### application.yml 关键配置

```yaml
server:
  port: 8080
spring:
  profiles:
    active: dev                    # 默认开发环境
  application:
    name: quickboot
quickboot:
  cors:
    enabled: true                  # CORS 跨域开关
    allowed-origins: "*"
    allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
  trace:
    enabled: true                  # MDC 链路追踪开关
  redis:
    enabled: false                 # Redis 缓存开关
  sa-token:
    enabled: false                 # Sa-Token 鉴权开关
  mybatis-plus:
    enabled: false                 # MyBatis-Plus ORM 开关（需配合数据源使用）
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics  # Actuator 暴露端点
  endpoint:
    health:
      show-details: when-authorized   # 健康详情仅授权可见
```

### logback-spring.xml

- 日志格式: `%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] [%X{traceId}] %logger{36} - %msg%n`
- `[%X{traceId}]` 为 MDC 占位符，从 `TraceIdFilter` 写入的 ThreadLocal 取值
- 输出: CONSOLE + FILE 双 appender
- 滚动策略: 按天滚动，保留 14 天
- 日志路径: `logs/quickboot.log`

## 关键设计模式与约定

### 1. 条件装配模式

所有可插拔组件使用 `@ConditionalOnClass` + `@ConditionalOnProperty` 双重条件：
- `@ConditionalOnClass(name="...")` 使用**字符串形式**指定类名，通过 ASM 读取字节码判定，避免类加载触发 `NoClassDefFoundError`
- `@ConditionalOnProperty` 通过配置开关控制运行时是否装配
- `matchIfMissing=true` 用于默认开启的特性（CORS、TraceId）
- MyBatis-Plus 额外依赖主类 `exclude MybatisPlusAutoConfiguration` 作为第三层守卫，防止未启用时自动装配初始化失败

### 2. 可插拔架构设计

- optional 依赖阻断了传递性，下游模块需显式引入
- 启动类排除 `DataSourceAutoConfiguration` / `MybatisPlusAutoConfiguration` / `RedisAutoConfiguration`，确保最小依赖可启动
- `@Bean` 方法参数注入避免类字段类型解析

### 3. MDC 链路追踪

- `TraceIdFilter`（`@Order(HIGHEST_PRECEDENCE)`）在请求最早期写入 traceId 到 MDC
- 优先从 `X-Trace-Id` 请求头获取（支持上游透传），否则生成 32 位 UUID
- 请求结束时 `MDC.clear()` 清理（防止线程池复用导致 ThreadLocal 泄漏）
- traceId 同时回写响应头，便于前端关联

### 4. 统一 JSON 序列化

`JacksonConfig` 通过 `Jackson2ObjectMapperBuilderCustomizer`（而非覆盖 ObjectMapper Bean）定制全局序列化：
- 禁用 `WRITE_DATES_AS_TIMESTAMPS`，统一日期格式化
- `LocalDateTime` → `yyyy-MM-dd HH:mm:ss`，`LocalDate` → `yyyy-MM-dd`，`LocalTime` → `HH:mm:ss`
- `Long` / `long` → String（防止 JS 精度丢失）
- `NON_NULL` 序列化（忽略 null 字段）
- 时区统一 `Asia/Shanghai`
- `failOnUnknownProperties(false)`（忽略未知字段，保证向前兼容）

### 5. 统一枚举序列化

`ErrorCode` 同时实现 `IErrorCode`（提供 `getCode`/`getMessage`）和 `BaseEnum`（提供 `getCode`/`getDescription`），业务枚举可参照此模式统一管理编码与描述。

### 6. API 响应规范

- 所有 Controller 方法返回 `ApiResponse<T>` 统一格式
- 成功: `ApiResponse.success(data)` → `{code: 0, message: "success", data: ...}`
- 失败: `ApiResponse.fail(code, message)` → `{code: xxx, message: "...", data: null}`

### 7. 异常处理优先级

- `SaTokenExceptionHandler`（`@Order(HIGHEST_PRECEDENCE)`）优先捕获鉴权异常
- `GlobalExceptionHandler` 兜底处理业务异常、校验异常、404、500 等
- 所有异常统一转换为 `ApiResponse` 返回，不暴露堆栈

### 8. 分层规则

- **Controller** 只做请求接收和响应返回，不含业务逻辑
- **Application Service** 编排业务流程，调用领域层和基础设施层
- **Domain Model** 是纯数据对象，不含 Spring 注解
- **Infrastructure** 处理技术细节（数据库、缓存、鉴权等）

## 文件清单

```
quickboot-template-springboot2-jdk8/
├── pom.xml                                    # 父 POM（依赖管理、模块声明）
├── Dockerfile                                 # 多阶段 Docker 构建
├── docker-compose.yml                         # Docker Compose 编排
├── .dockerignore
├── .gitignore
├── README.md
├── SKILL.md                                   # 本文件
│
├── quickboot-common/                          # 公共层（11 个类 + 6 个测试）
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/quickboot/common/
│       │   ├── api/
│       │   │   ├── ApiResponse.java           # 统一响应封装
│       │   │   ├── BasePageRequest.java        # 分页请求基类
│       │   │   ├── ErrorCode.java              # 错误码枚举
│       │   │   ├── IErrorCode.java             # 错误码接口
│       │   │   └── PageResponse.java           # 分页响应封装
│       │   ├── enums/
│       │   │   └── BaseEnum.java               # 枚举序列化接口
│       │   ├── exception/
│       │   │   └── BusinessException.java       # 业务异常
│       │   └── util/
│       │       ├── CollectionUtils.java         # 集合工具
│       │       ├── DateUtils.java               # 日期工具
│       │       ├── JsonUtils.java               # JSON 工具
│       │       └── StringUtils.java             # 字符串工具
│       └── test/java/com/quickboot/common/
│           ├── api/
│           │   ├── ApiResponseTest.java
│           │   └── ErrorCodeTest.java
│           ├── exception/
│           │   └── BusinessExceptionTest.java
│           └── util/
│               ├── DateUtilsTest.java
│               ├── JsonUtilsTest.java
│               └── StringUtilsTest.java
│
├── quickboot-domain/                          # 领域层（1 个类）
│   ├── pom.xml
│   └── src/main/java/com/quickboot/domain/
│       └── model/HealthStatus.java             # 健康状态领域模型
│
├── quickboot-infrastructure/                   # 基础设施层（3 个类）
│   ├── pom.xml
│   └── src/main/java/com/quickboot/infrastructure/
│       └── config/
│           ├── MybatisPlusConfig.java          # MyBatis-Plus 配置 + 分页拦截器
│           ├── RedisConfig.java                # Redis 配置（可插拔）
│           └── SaTokenConfig.java              # Sa-Token 配置（可插拔）
│
├── quickboot-application/                      # 应用服务层（1 个类）
│   ├── pom.xml
│   └── src/main/java/com/quickboot/application/
│       └── service/HealthService.java           # 健康检查服务
│
└── quickboot-web/                              # Web 入口层（7 个主类 + 4 个测试类）
    ├── pom.xml                                 # 含 spring-boot-maven-plugin
    ├── logs/
    │   └── quickboot.log
    └── src/
        ├── main/
        │   ├── java/com/quickboot/web/
        │   │   ├── QuickbootApplication.java    # 启动入口
        │   │   ├── config/
        │   │   │   ├── CorsConfig.java          # 跨域配置
        │   │   │   └── JacksonConfig.java       # Jackson 序列化配置
        │   │   ├── filter/
        │   │   │   └── TraceIdFilter.java       # MDC 链路追踪过滤器
        │   │   ├── controller/
        │   │   │   └── HealthController.java    # 健康检查接口
        │   │   └── handler/
        │   │       ├── GlobalExceptionHandler.java       # 全局异常处理
        │   │       └── SaTokenExceptionHandler.java      # Sa-Token 异常处理（可插拔）
        │   └── resources/
        │       ├── application.yml              # 主配置
        │       ├── application-dev.yml          # 开发环境
        │       ├── application-test.yml         # 测试环境
        │       ├── application-prod.yml         # 生产环境
        │       └── logback-spring.xml           # 日志配置（含 traceId MDC）
        └── test/java/com/quickboot/web/
            ├── HealthControllerTest.java         # Web 层切片测试
            ├── QuickbootContextTest.java         # 全量上下文加载测试
            ├── handler/GlobalExceptionHandlerTest.java  # 异常处理器单元测试
            └── generator/CodeGenerator.java      # MyBatis-Plus 代码生成器
```

## 构建与运行

### 本地构建

```bash
# 编译打包（跳过测试）
mvn clean package -DskipTests

# 运行测试
mvn test

# 启动应用（从 web 模块的 jar 启动）
java -jar quickboot-web/target/quickboot-web-1.0.0-SNAPSHOT.jar

# 指定环境启动
java -Dspring.profiles.active=prod -jar quickboot-web/target/quickboot-web-1.0.0-SNAPSHOT.jar

# 健康检查
curl http://localhost:8080/health

# Actuator 监控端点
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics
```

### Docker 部署

项目内置多阶段 Dockerfile 和 docker-compose.yml：

**Dockerfile**（多阶段构建）：
- Stage 1: `maven:3.9-eclipse-temurin-8` 编译打包，利用 pom.xml 层缓存加速依赖下载
- Stage 2: `eclipse-temurin:8-jre-alpine` 运行，时区设为 Asia/Shanghai，JVM 参数含 G1GC + MaxRAMPercentage=75%

**docker-compose.yml**：
- `app` 服务：端口 8080，默认 `SPRING_PROFILES_ACTIVE=prod`，Redis/Sa-Token 开关通过环境变量控制
- `redis` 服务（可选，默认注释）：取消注释并配置 `QUICKBOOT_REDIS_ENABLED=true` 即可启用
- `mysql` 服务（可选，默认注释）：取消注释并在 `application-prod.yml` 配置数据源

```bash
# Docker 构建
docker build -t quickboot .

# Docker 运行
docker run -p 8080:8080 quickboot

# Docker Compose 启动
docker-compose up -d
```

### 代码生成器

`quickboot-web/src/test/.../generator/CodeGenerator.java` 提供 MyBatis-Plus 代码生成功能：
1. 修改 `JDBC_URL`、`JDBC_USERNAME`、`JDBC_PASSWORD` 为真实数据库连接
2. 在 `TABLES` 中填入表名，按需调整 `TABLE_PREFIX`
3. 运行 `main()` 方法，生成 Entity / Mapper / Service / Controller 代码
4. 生成策略：下划线转驼峰、启用 Lombok、逻辑删除 `deleted`、乐观锁 `version`、Velocity 模板

## 开发新功能指南

当需要为项目添加新功能时，按以下分层模式添加代码：

1. **公共层** (`quickboot-common`): 跨模块复用的通用类放在对应子包——`api`（响应/请求封装）、`enums`（枚举接口）、`exception`（异常）、`util`（工具类）
2. **领域层** (`quickboot-domain`): 在 `domain.model` 下创建实体类（纯 POJO + Lombok，不含 Spring 注解）
3. **基础设施层** (`quickboot-infrastructure`): 在 `infrastructure.mapper` 下创建 Mapper 接口（继承 `BaseMapper`），在 `infrastructure.config` 下添加技术配置类
4. **应用层** (`quickboot-application`): 在 `application.service` 下创建 Service 类（`@Service`），编排业务流程
5. **Web 层** (`quickboot-web`): 在 `web.controller` 下创建 Controller（`@RestController` + `@Validated`），在 `web.config` 下添加 Web 配置，在 `web.handler` 下添加异常处理器
6. **测试**: common 模块工具类用 JUnit 5 + AssertJ 单元测试；web 模块 Controller 用 `@WebMvcTest` 切片测试 + `@MockBean` 隔离 Service

**命名约定**: Controller 使用 `@RestController`，Service 使用 `@Service`，Config 使用 `@Configuration`。所有 Bean 优先使用构造器注入。

**可插拔组件约定**: 新增可选功能组件时，遵循 optional 依赖 + `@ConditionalOnClass(name=字符串)` + `@ConditionalOnProperty` 三重保护模式，确保未引入依赖时不触发类加载错误。
