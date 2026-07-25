# QuickBoot

> 基于 Spring Boot 2.7 的多模块 DDD 分层架构脚手架

QuickBoot 是一个基于 **Spring Boot 2.7.18 + JDK 8** 的多模块 Maven 脚手架项目，采用 **DDD（领域驱动设计）分层架构**，开箱即用。内置统一响应封装、全局异常处理、参数校验、分页查询、链路追踪、可插拔的 Sa-Token 鉴权与 Redis 缓存等企业级能力，并集成 MyBatis-Plus 代码生成器与 Docker 多阶段构建，助力团队快速搭建规范的后端服务。

- **基础包名**：`com.quickboot`
- **服务端口**：`8080`
- **版本**：`1.0.0-SNAPSHOT`
- **默认环境**：`dev`（开箱即用，无需数据库/Redis 即可启动）

---

## 目录

- [技术栈](#技术栈)
- [模块架构](#模块架构)
- [核心功能清单](#核心功能清单)
- [特性开关一览表](#特性开关一览表)
- [可插拔功能使用指南](#可插拔功能使用指南)
- [快速开始](#快速开始)
- [多环境配置](#多环境配置)
- [Docker 部署](#docker-部署)
- [代码生成器](#代码生成器)
- [API 响应规范](#api-响应规范)
- [错误码表](#错误码表)
- [目录结构](#目录结构)
- [开发约定](#开发约定)

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 应用框架（父 POM 统一管理版本） |
| JDK | 1.8 | Java 运行环境 |
| MyBatis-Plus | 3.5.5 | ORM 框架（含分页插件、代码生成器） |
| Sa-Token | 1.37.0 | 轻量级认证鉴权框架（可插拔） |
| Lombok | 1.18.32 | 代码简化（注解生成 getter/setter 等） |
| spring-boot-starter-web | 随 Spring Boot | Web MVC |
| spring-boot-starter-validation | 随 Spring Boot | Bean Validation 参数校验 |
| spring-boot-starter-actuator | 随 Spring Boot | 监控端点 |
| spring-boot-starter-data-redis | 随 Spring Boot | Redis 缓存（可插拔） |
| Velocity | 2.3 | 代码生成模板引擎（仅测试期使用） |
| Logback | 随 Spring Boot | 日志框架（控制台 + 滚动文件） |

---

## 模块架构

项目采用 **5 模块 DDD 分层架构**，依赖方向自上而下：

```
┌─────────────────────────────────────────────────────────────┐
│                    quickboot-web                             │  HTTP 入口层
│        Controller / 全局异常处理 / 过滤器 / 启动类            │
└────────────────────────┬────────────────────────────────────┘
                         │ 依赖
┌────────────────────────▼────────────────────────────────────┐
│                quickboot-application                         │  应用服务层
│              编排业务逻辑，协调领域层与基础设施层              │
└────────────────────────┬────────────────────────────────────┘
                         │ 依赖
┌────────────────────────▼────────────────────────────────────┐
│              quickboot-infrastructure                        │  基础设施层
│      数据访问 / MyBatis-Plus / Redis / Sa-Token 配置         │
└────────────────────────┬────────────────────────────────────┘
                         │ 依赖
┌────────────────────────▼────────────────────────────────────┐
│                   quickboot-domain                           │  领域层
│                领域模型（纯 POJO，无 Spring 依赖）            │
└────────────────────────┬────────────────────────────────────┘
                         │ 依赖
┌────────────────────────▼────────────────────────────────────┐
│                   quickboot-common                           │  公共层
│      统一响应 / 错误码 / 异常 / 工具类（跨模块复用）          │
└─────────────────────────────────────────────────────────────┘
```

> 注：`quickboot-web` 同时直接依赖 `quickboot-common`；`quickboot-application` 同时直接依赖 `quickboot-common` 与 `quickboot-domain`。

### 各模块职责

| 模块 | 包路径 | 职责 |
|------|--------|------|
| **quickboot-common** | `com.quickboot.common` | 公共层。提供统一响应封装、错误码枚举、业务异常、分页封装与各类工具类，供所有模块复用 |
| **quickboot-domain** | `com.quickboot.domain` | 领域层。定义领域模型（纯 POJO，不含 Spring 注解），表达业务核心概念 |
| **quickboot-infrastructure** | `com.quickboot.infrastructure` | 基础设施层。处理技术细节：MyBatis-Plus 配置、Mapper 扫描、Redis 与 Sa-Token 的可插拔配置 |
| **quickboot-application** | `com.quickboot.application` | 应用服务层。编排业务流程，调用领域层与基础设施层完成用例 |
| **quickboot-web** | `com.quickboot.web` | Web 入口层。暴露 HTTP 接口、启动入口、全局异常处理、过滤器与 Web 配置；打包入口在此模块 |

---

## 核心功能清单

| 序号 | 功能 | 说明 | 默认状态 |
|------|------|------|----------|
| 1 | 多模块 DDD 分层架构 | 5 模块分层，职责隔离，依赖方向清晰 | ✅ 启用 |
| 2 | 统一 API 响应封装 | `ApiResponse<T>` 统一返回 `code/message/data` | ✅ 启用 |
| 3 | 统一错误码枚举 | `ErrorCode` 定义 8 种标准错误码 | ✅ 启用 |
| 4 | 业务异常 + 全局异常处理 | `BusinessException` 配合 `GlobalExceptionHandler` 统一兜底 | ✅ 启用 |
| 5 | 参数校验 | 基于 Bean Validation（`@Valid`、`@Min`、`@Max` 等） | ✅ 启用 |
| 6 | 分页查询封装 | `BasePageRequest`（请求）+ `PageResponse`（响应） | ✅ 启用 |
| 7 | MyBatis-Plus 集成（可插拔） | 分页插件 + `@MapperScan` 自动扫描 Mapper，默认关闭，无数据库时回退内存模拟 | ⚪ 默认关闭 |
| 8 | 代码生成器 | `CodeGenerator` 一键生成 Entity/Mapper/Service/Controller | ✅ 提供 |
| 9 | Sa-Token 认证（可插拔） | 拦截器 + 专属异常处理，按需开启 | ⚪ 默认关闭 |
| 10 | Redis 缓存（可插拔） | 自定义 `RedisTemplate` + 声明式缓存，按需开启 | ⚪ 默认关闭 |
| 11 | 全局 Jackson 序列化 | 日期格式化、Long 转字符串、忽略 null、时区统一 | ✅ 启用 |
| 12 | CORS 跨域 | 全局跨域配置，支持凭证模式 | ✅ 启用 |
| 13 | 链路追踪 TraceId | `TraceIdFilter` 写入 MDC + 回写响应头 | ✅ 启用 |
| 14 | Actuator 监控端点 | 暴露 health / info / metrics | ✅ 启用 |
| 15 | 多环境配置 | dev / test / prod 三套 Profile | ✅ 启用 |
| 16 | Docker 容器化部署 | 多阶段构建镜像 + docker-compose 编排 | ✅ 提供 |

---

## 特性开关一览表

QuickBoot 通过 `@ConditionalOnProperty` / `@ConditionalOnClass` 实现功能的按需装配，在 `application.yml` 的 `quickboot` 节点下统一控制：

| 开关 | 默认值 | 说明 | 启用方式 |
|------|--------|------|----------|
| `quickboot.cors.enabled` | `true` | 跨域 CORS 配置（`CorsConfig`） | 设为 `false` 可关闭 |
| `quickboot.trace.enabled` | `true` | 链路追踪 `TraceIdFilter`（MDC + 响应头） | 设为 `false` 可关闭 |
| `quickboot.redis.enabled` | `false` | Redis 缓存（`RedisConfig`） | 引入依赖 + 设为 `true` + 配置 `spring.redis.host` |
| `quickboot.mybatis-plus.enabled` | `false` | MyBatis-Plus ORM（`MybatisPlusConfig`） | 设为 `true` + 配置数据源 |
| `quickboot.sa-token.enabled` | `false` | Sa-Token 鉴权（`SaTokenConfig` / `SaTokenExceptionHandler`） | 引入依赖 + 设为 `true` |

> CORS 与 TraceId 默认开启，无需额外依赖即可使用；Redis、Sa-Token 与 MyBatis-Plus 默认关闭，**不引入依赖或未配置数据源时不会报错**，启动零依赖。

---

## 可插拔功能使用指南

Sa-Token 与 Redis 采用「编译期可见 + 运行期条件装配」的设计：依赖在 `quickboot-infrastructure` 中声明为 `optional`，仅当 Web 层显式引入且配置开关打开时才真正生效。

### Sa-Token 认证

**步骤 1**：在 `quickboot-web/pom.xml` 中添加 Sa-Token 依赖（infrastructure 已声明 optional，需在 web 显式引入以使其进入运行时 classpath）：

```xml
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>${sa-token.version}</version>
</dependency>
```

**步骤 2**：在 `application.yml`（或对应环境配置）中启用开关：

```yaml
quickboot:
  sa-token:
    enabled: true
```

启用后效果：
- `SaTokenConfig` 装配拦截器，拦截 `/api/**`，放行 `/health`、`/actuator/**`，默认对所有 API 执行登录校验。
- `SaTokenExceptionHandler` 以最高优先级处理未登录（401）、无权限（403）、无角色（403）异常，返回统一响应。

### Redis 缓存

**步骤 1**：在 `quickboot-web/pom.xml` 中添加 Redis 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**步骤 2**：在 `application.yml` 中启用开关并配置连接信息：

```yaml
quickboot:
  redis:
    enabled: true

spring:
  redis:
    host: localhost
    port: 6379
    # password: 你的密码
    # database: 0
```

启用后效果：
- `RedisConfig` 装配自定义 `RedisTemplate`（key 为 String 序列化，value 为 JSON 序列化）与 `StringRedisTemplate`。
- 开启 `@EnableCaching`，`@Cacheable` / `@CacheEvict` 等注解生效。

> 启动类已排除 `RedisAutoConfiguration`，因此未启用时即使引入依赖也不会自动连接 Redis。**不需要时保持默认即可，启动不会报错。**

### MyBatis-Plus ORM

MyBatis-Plus 默认关闭，`UserService` 在无数据库时自动回退到内存模拟模式，项目可零依赖启动。

**启用方式**：在 `application.yml` 中开启开关并配置数据源，同时在 `quickboot-web/pom.xml` 中添加 MySQL 驱动依赖：

```yaml
quickboot:
  mybatis-plus:
    enabled: true

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quickboot?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

启用后：`MybatisPlusConfig` 装配分页插件与 `@MapperScan`，Service 自动切换为数据库模式。

> 未启用时无需数据源，Service 使用内存 Map 模拟存储。**启动类已排除 `MybatisPlusAutoConfiguration`，未启用时不会尝试连接数据库。**

### 新增实体开发指南（MyBatis-Plus 启用后）

启用后新增数据库实体，按 DDD 分层依次创建以下文件：

| 层 | 文件 | 包路径 | 要点 |
|---|---|---|---|
| **Domain** | `Order.java` | `domain.model` | 纯 POJO，不含持久化注解 |
| **Entity** | `OrderEntity.java` | `infrastructure.entity` | 使用 `@TableName`、`@TableId`、`@TableLogic`、`@Version` 等 MyBatis-Plus 注解 |
| **Mapper** | `OrderMapper.java` | `infrastructure.mapper` | 继承 `BaseMapper<T>` 获得单表 CRUD，加 `@Mapper` 注解 |
| **Service** | `OrderService.java` | `application.service` | 注入 Mapper，负责 Entity ↔ Domain 转换，不暴露 Entity 到上层 |
| **Controller** | `OrderController.java` | `web.controller` | 只调用 Service，统一返回 `ApiResponse<T>` |

分层调用关系：`Controller → Service → Mapper(BaseMapper<T>)`

```java
// 示例：Mapper 继承 BaseMapper 即可获得 insert / selectById / updateById / deleteById / selectPage 等方法
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
    // 如需自定义 SQL，可添加 @Select / @Update 等注解方法
}
```

> 项目内置了 [CodeGenerator](#代码生成器) 代码生成器，可根据数据库表一键生成上述全部分层代码，适合表结构已确定的场景。若需精细控制业务逻辑（如内存回退），建议手动创建。

---

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+

### 编译运行

```bash
# 编译打包（跳过测试）
mvn clean package -DskipTests

# 运行（从 web 模块 jar 启动）
java -jar quickboot-web/target/quickboot-web-1.0.0-SNAPSHOT.jar

# 健康检查
curl http://localhost:8080/health
```

健康检查预期返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "status": "UP",
    "application": "quickboot",
    "checkedAt": "2026-07-24 12:00:00"
  }
}
```

### 指定运行环境

```bash
# 通过启动参数指定 Profile
java -jar quickboot-web/target/quickboot-web-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod

# 或通过环境变量
SPRING_PROFILES_ACTIVE=prod java -jar quickboot-web/target/quickboot-web-1.0.0-SNAPSHOT.jar
```

### 运行测试

```bash
mvn test
```

---

## 多环境配置

项目提供三套 Profile，默认激活 `dev`。配置文件位于 `quickboot-web/src/main/resources/`：

| Profile | 文件 | 特征 | 适用场景 |
|---------|------|------|----------|
| `dev` | `application-dev.yml` | 开启懒加载加速启动；业务包日志 `DEBUG`，框架包 `INFO` | 本地开发调试 |
| `test` | `application-test.yml` | 业务包日志 `INFO`，框架包收敛到 `WARN` | 测试 / 预发环境 |
| `prod` | `application-prod.yml` | 日志收敛到 `INFO`/`WARN`，减少日志量 | 生产环境 |

切换方式：

```bash
# 命令行参数
--spring.profiles.active=prod

# 环境变量
SPRING_PROFILES_ACTIVE=prod

# 或修改 application.yml 默认值
spring:
  profiles:
    active: prod
```

---

## Docker 部署

项目提供多阶段构建的 `Dockerfile`（Stage1 Maven 编译，Stage2 JRE Alpine 运行）与 `docker-compose.yml`。

```bash
# 方式一：手动构建镜像
docker build -t quickboot .

# 运行容器
docker run -d -p 8080:8080 quickboot

# 方式二：使用 docker-compose
docker-compose up -d
```

镜像特性：
- **多阶段构建**：构建阶段使用 `maven:3.9-eclipse-temurin-8`，运行阶段使用 `eclipse-temurin:8-jre-alpine`，最小化镜像体积。
- **JVM 优化**：默认使用 G1GC，堆内存限制为容器可用内存的 75%。
- **时区**：内置 `Asia/Shanghai` 时区。

通过环境变量覆盖配置（docker-compose 已示例）：

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - QUICKBOOT_REDIS_ENABLED=false
  - QUICKBOOT_SA_TOKEN_ENABLED=false
```

> `docker-compose.yml` 中还内置了注释形式的 Redis 与 MySQL 服务，按需取消注释即可启用。

---

## 代码生成器

`CodeGenerator` 基于 MyBatis-Plus Generator + Velocity 模板，位于测试目录，不参与运行时打包。它的作用是根据数据库表结构，自动生成 Entity、Mapper、Service、Controller 等分层代码文件，避免手动编写重复的 CRUD 代码。

**位置**：`quickboot-web/src/test/java/com/quickboot/web/generator/CodeGenerator.java`

> **何时使用**：表结构已确定，需快速生成全套 CRUD 代码时使用。若项目未启用 MyBatis-Plus（默认关闭），或需要精细控制业务逻辑（如内存回退模式），可跳过本节，参考 [新增实体开发指南](#新增实体开发指南mybatis-plus-启用后) 手动创建。

### 使用步骤

1. **修改数据源配置**：编辑类中常量 `JDBC_URL`、`JDBC_USERNAME`、`JDBC_PASSWORD` 为真实数据库连接信息。
2. **填写表名**：在 `TABLES` 数组中填入需要生成代码的表名（如 `{"t_user"}`）。
3. **按需调整**：`TABLE_PREFIX`（表名前缀，生成时自动剔除，默认 `t_`）、`OUTPUT_DIR`（代码输出目录）。
4. **运行**：右键运行 `main` 方法即可生成全部分层代码。

### 生成策略

- 父包：`com.quickboot`，按层分包（entity / mapper / service / controller）。
- 命名：数据库下划线转驼峰；启用 Lombok。
- 实体：逻辑删除字段 `deleted`，乐观锁字段 `version`，文件名格式 `XxxEntity`。
- Controller：Rest 风格（`@RestController`）。

### 生成的代码结构（以表 `t_user` 为例）

```
com.quickboot
├── entity
│   └── UserEntity.java          // 实体类（含 Lombok、逻辑删除、乐观锁）
├── mapper
│   └── UserMapper.java          // Mapper 接口（继承 BaseMapper）
├── service
│   ├── UserService.java         // Service 接口（继承 IService）
│   └── impl
│       └── UserServiceImpl.java // Service 实现（继承 ServiceImpl）
└── controller
    └── UserController.java      // RestController（继承 BaseController）
resources/mapper
    └── UserMapper.xml           // Mapper XML（自定义 SQL）
```

> 生成的代码默认输出到 web 模块的 `src/main/java`。多模块场景下，请按 DDD 分层将 Entity 与 Mapper 输出到 infrastructure、Service 输出到 application、Controller 输出到 web。

---

## API 响应规范

所有 Controller 方法统一返回 `ApiResponse<T>`，结构如下：

### 统一响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "status": "UP",
    "application": "quickboot",
    "checkedAt": "2026-07-24 12:00:00"
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码，`0` 表示成功，其余为错误码 |
| `message` | String | 响应消息 |
| `data` | T | 响应数据，失败时为 `null` |

### 分页响应格式

分页查询使用 `PageResponse<T>` 作为 `data`：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `records` | List | 当前页数据列表 |
| `total` | long | 总记录数 |
| `current` | long | 当前页码 |
| `size` | long | 每页大小 |
| `pages` | long | 总页数 |

### 使用方式

```java
// 成功（携带数据）
return ApiResponse.success(data);

// 成功（无数据）
return ApiResponse.success();

// 失败
return ApiResponse.fail(ErrorCode.BAD_REQUEST.getCode(), "参数不合法");

// 抛出业务异常（由全局异常处理器统一捕获）
throw new BusinessException(ErrorCode.NOT_FOUND);
```

---

## 错误码表

`ErrorCode` 枚举定义了系统通用错误码，同时实现 `IErrorCode` 与 `BaseEnum`：

| 枚举值 | 错误码 | 消息 | 说明 |
|--------|--------|------|------|
| `SUCCESS` | 0 | success | 请求成功 |
| `BAD_REQUEST` | 400 | bad request | 错误请求（参数不合法等） |
| `UNAUTHORIZED` | 401 | unauthorized | 未认证（需要登录） |
| `FORBIDDEN` | 403 | forbidden | 无权限访问 |
| `NOT_FOUND` | 404 | not found | 资源不存在 |
| `TOO_MANY_REQUESTS` | 429 | too many requests | 请求过于频繁（限流） |
| `INTERNAL_ERROR` | 500 | internal server error | 服务器内部错误 |
| `SERVICE_UNAVAILABLE` | 503 | service unavailable | 服务不可用 |

### 全局异常处理映射

`GlobalExceptionHandler` 统一拦截异常并转换为上述错误码：

| 异常类型 | 响应错误码 |
|----------|------------|
| `BusinessException` | 异常自身携带的 code |
| `MethodArgumentNotValidException` / `BindException` | 400 BAD_REQUEST |
| `NoHandlerFoundException` | 404 NOT_FOUND |
| `HttpRequestMethodNotSupportedException` | 400 BAD_REQUEST |
| 其他未捕获异常 | 500 INTERNAL_ERROR |
| Sa-Token `NotLoginException` | 401 UNAUTHORIZED |
| Sa-Token `NotPermissionException` / `NotRoleException` | 403 FORBIDDEN |

> 业务扩展错误码可实现 `IErrorCode` 接口自定义枚举，配合 `BusinessException(IErrorCode)` 抛出。

---

## 目录结构

```
quickboot-template-springboot2-jdk8/
├── pom.xml                                      # 父 POM（依赖管理、模块声明）
├── SKILL.md                                     # 项目结构化认知指南
├── Dockerfile                                   # 多阶段构建镜像
├── docker-compose.yml                           # 容器编排（含可选 Redis/MySQL）
├── .dockerignore
├── .gitignore
│
├── quickboot-common/                            # 【公共层】跨模块复用的通用类
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/quickboot/common/
│       │   ├── api/
│       │   │   ├── ApiResponse.java             # 统一 API 响应封装
│       │   │   ├── BasePageRequest.java         # 分页请求基类
│       │   │   ├── ErrorCode.java               # 错误码枚举
│       │   │   ├── IErrorCode.java              # 错误码接口
│       │   │   └── PageResponse.java            # 分页响应封装
│       │   ├── enums/
│       │   │   └── BaseEnum.java                # 枚举序列化接口
│       │   ├── exception/
│       │   │   └── BusinessException.java       # 业务异常
│       │   └── util/
│       │       ├── CollectionUtils.java         # 集合工具
│       │       ├── DateUtils.java               # 日期时间工具
│       │       ├── JsonUtils.java               # JSON 工具（基于 Jackson）
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
├── quickboot-domain/                            # 【领域层】领域模型
│   ├── pom.xml
│   └── src/main/java/com/quickboot/domain/
│       └── model/
│           └── HealthStatus.java                # 健康状态领域模型
│
├── quickboot-infrastructure/                    # 【基础设施层】技术细节
│   ├── pom.xml
│   └── src/main/java/com/quickboot/infrastructure/
│       └── config/
│           ├── MybatisPlusConfig.java           # MyBatis-Plus 配置（分页插件 + MapperScan）
│           ├── RedisConfig.java                 # Redis 可插拔配置
│           └── SaTokenConfig.java               # Sa-Token 可插拔配置
│
├── quickboot-application/                       # 【应用服务层】业务编排
│   ├── pom.xml
│   └── src/main/java/com/quickboot/application/
│       └── service/
│           └── HealthService.java               # 健康检查应用服务
│
└── quickboot-web/                               # 【Web 入口层】HTTP 接口与启动
    ├── pom.xml
    ├── logs/                                    # 运行时日志输出目录
    │   └── quickboot.log
    └── src/
        ├── main/
        │   ├── java/com/quickboot/web/
        │   │   ├── QuickbootApplication.java    # Spring Boot 启动类
        │   │   ├── config/
        │   │   │   ├── CorsConfig.java          # 跨域 CORS 配置
        │   │   │   └── JacksonConfig.java       # 全局 Jackson 序列化配置
        │   │   ├── controller/
        │   │   │   └── HealthController.java    # 健康检查接口（GET /health）
        │   │   ├── filter/
        │   │   │   └── TraceIdFilter.java       # 链路追踪过滤器
        │   │   └── handler/
        │   │       ├── GlobalExceptionHandler.java  # 全局异常处理
        │   │       └── SaTokenExceptionHandler.java  # Sa-Token 异常处理（可插拔）
        │   └── resources/
        │       ├── application.yml              # 主配置（端口、特性开关、Actuator）
        │       ├── application-dev.yml          # 开发环境配置
        │       ├── application-test.yml         # 测试环境配置
        │       ├── application-prod.yml         # 生产环境配置
        │       └── logback-spring.xml           # 日志配置（控制台 + 滚动文件）
        └── test/java/com/quickboot/web/
            ├── generator/
            │   └── CodeGenerator.java           # MyBatis-Plus 代码生成器
            ├── handler/
            │   └── GlobalExceptionHandlerTest.java
            ├── HealthControllerTest.java        # 健康检查接口测试
            └── QuickbootContextTest.java        # 上下文加载测试
```

---

## 开发约定

### 依赖注入

- **统一使用构造器注入**，禁止使用字段注入（`@Autowired` on field）。
- 仅有一个构造器时可省略 `@Autowired` 注解；配合 Lombok `@RequiredArgsConstructor` 可简化 final 字段的构造器生成。

```java
@RestController
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }
}
```

### 分层规则

| 层 | 规则 |
|----|------|
| **Controller（web）** | 只做请求接收与响应返回，不含业务逻辑；返回值统一为 `ApiResponse<T>` |
| **Application Service（application）** | 编排业务流程，调用领域层与基础设施层；使用 `@Service` |
| **Domain Model（domain）** | 纯数据对象，不含 Spring 注解；新增实体放在 `model` 子包 |
| **Infrastructure（infrastructure）** | 处理技术细节；Mapper 接口放在 `infrastructure.mapper` 包下；配置类放在 `config` 包下 |
| **Common（common）** | 跨模块复用的通用类；工具类设私有构造器，不可实例化 |

### 命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Controller | `@RestController`，类名 `XxxController` | `HealthController` |
| Service | `@Service`，类名 `XxxService` | `HealthService` |
| Config | `@Configuration`，类名 `XxxConfig` | `RedisConfig` |
| Mapper | 接口，类名 `XxxMapper`，放在 `infrastructure.mapper` 包 | `UserMapper` |
| 实体 | 领域模型放在 `domain.model` 包 | `HealthStatus` |
| 错误码 | 实现 `IErrorCode` 的枚举 | `ErrorCode` |

### Lombok 使用

- 实体 / DTO / 响应对象使用 `@Data`、`@NoArgsConstructor`、`@AllArgsConstructor`。
- 工具类使用 `final` 修饰 + 私有构造器，禁止实例化。
- 需要构造器注入的类可使用 `@RequiredArgsConstructor`（配合 `final` 字段）。

### 新增功能开发流程

1. **领域层**（`quickboot-domain`）：在 `domain.model` 下创建实体类。
2. **基础设施层**（`quickboot-infrastructure`）：在 `infrastructure.mapper` 下创建 Mapper，按需在 `config` 下添加配置。
3. **应用层**（`quickboot-application`）：在 `application.service` 下创建服务类。
4. **Web 层**（`quickboot-web`）：在 `web.controller` 下创建 Controller。
5. **公共类**（`quickboot-common`）：跨模块复用的类放在此处。

### 日志规范

- 日志格式：`时间 级别 [线程] [traceId] logger - 消息`，`traceId` 由 `TraceIdFilter` 自动写入 MDC。
- 日志输出：控制台 + 文件双 appender，文件按天滚动，保留 14 天。
- 各环境日志级别由对应 Profile 配置控制。

---

*QuickBoot — 让后端项目快速起步，规范先行。*
