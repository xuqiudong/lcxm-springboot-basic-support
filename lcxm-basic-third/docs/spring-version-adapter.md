# Spring / JDK 低版本适配说明

当前 `lcxm-basic-third` 主线面向：

- JDK 21
- Spring Boot 3.x
- Spring Framework 6.x
- Servlet `jakarta.servlet`

如果复制到 JDK 8 Maven 项目，或后续做 Spring MVC 4.x/5.x 适配版，重点处理下面这些差异。

## 1. Java 版本

目标项目改为 JDK 8 编译：

```xml
<maven.compiler.source>1.8</maven.compiler.source>
<maven.compiler.target>1.8</maven.compiler.target>
```

或沿用项目统一属性：

```xml
<java.version>1.8</java.version>
```

## 2. Servlet 包名

Spring Boot 3 / Spring 6 使用 `jakarta.servlet`。

Spring Boot 2 / Spring 5 / Spring MVC 4.x 使用 `javax.servlet`。

需要将：

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
```

改为：

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
```

依赖改为：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
```

如果目标容器较老，可按实际容器降到 `3.1.0`。

## 3. Spring 依赖

Spring Boot 2 项目：

- 使用 Boot 2.x BOM 管理 Spring 5.x。
- `@ConditionalOnMissingBean` 可继续使用。
- `WebMvcConfigurer` 注册 interceptor 的方式可继续使用。

普通 Spring MVC 4.x/5.x 项目：

- 不依赖 Spring Boot 条件注解。
- Java Config 项目可以继承当前抽象配置类后注册。
- XML 项目建议直接在 XML 中声明 `InboundTokenInterceptor`、`InboundTokenService`、`TokenStore` 等 bean。

Spring MVC 4.3 的 `HandlerInterceptor` 没有 default method，适配类需要补充：

```java
void postHandle(HttpServletRequest request,
                HttpServletResponse response,
                Object handler,
                ModelAndView modelAndView)

void afterCompletion(HttpServletRequest request,
                     HttpServletResponse response,
                     Object handler,
                     Exception ex)
```

## 4. 依赖版本

JDK 8 需要选择仍支持 JDK 8 的版本。

建议：

| 依赖 | JDK 21 主线 | JDK 8 建议 |
| --- | --- | --- |
| Caffeine | 3.x | 2.9.3 |
| Hutool | 5.8.x | 5.8.x 可继续使用 |
| Lombok | 1.18.x | 1.18.30+ |
| Jackson | 跟随 Boot 3 | 跟随目标 Spring/Boot 项目 |
| Servlet API | `jakarta.servlet-api` | `javax.servlet-api` |

Caffeine 示例：

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>2.9.3</version>
</dependency>
```

## 5. JDK API 检查

主代码当前基本未使用 JDK 9+ 集合工厂方法，但复制后仍建议全局检查：

- `Map.of(...)` -> `new HashMap<>()` 或 `Collections.singletonMap(...)`
- `List.of(...)` -> `Arrays.asList(...)`
- `Set.of(...)` -> `new HashSet<>(Arrays.asList(...))`
- `String.isBlank()` -> `StrUtil.isBlank(...)`
- `InputStream.readAllBytes()` -> Hutool / Commons IO / 手动读取
- `instanceof Xxx xxx` -> 普通 `instanceof` 后强转

`java.time.Duration` 是 JDK 8 API，可以保留。  
如果老项目配置绑定不方便，也可以在适配层改成毫秒数。

## 6. 当前模块重点改动位置

复制到 JDK 8 后优先检查：

```text
src/main/java
`-- cn/xuqiudong/basic/third
    |-- inbound/web/interceptor/InboundTokenInterceptor.java  # jakarta -> javax；Spring MVC 4.3 补全方法
    |-- config/spring/*                                      # Boot 条件注解按项目情况保留或移除
    |-- inbound/store/Caffeine*                              # Caffeine 版本降到 2.x
    `-- outbound/*                                           # 基本可复用，重点看依赖版本

src/test/java
`-- ...                                                      # Map.of/List.of 改为 JDK 8 写法
```

## 7. 推荐落地方式

如果只是某个 JDK 8 项目临时使用：

1. 复制 `lcxm-basic-third`。
2. 修改 `pom.xml` 的 Java、Servlet、Caffeine、Spring 版本。
3. 将 `jakarta.servlet` 改为 `javax.servlet`。
4. 删除或改造 `src/test/java` 中的 JDK 9+ 写法。
5. 先执行 `mvn -DskipTests compile`，按编译错误逐个适配。

如果后续要长期维护：

```text
lcxm-basic-third                  # 主线：Spring Boot 3 / JDK 21
lcxm-basic-third-springmvc4       # 适配版：Spring MVC 4.x / JDK 8
```

适配版只放低版本 Web/Spring 差异代码，核心 service、store、security、outbound 逻辑尽量复用或同步。
