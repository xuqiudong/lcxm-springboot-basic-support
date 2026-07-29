# Spring 低版本适配说明

当前 `lcxm-basic-third` 主线面向：

- JDK 21
- Spring Boot 3.x
- Spring Framework 6.x
- `jakarta.servlet`

如果后续需要适配 Spring MVC 4.x/5.x，需要单独处理以下点。

## 需要修改的点

### 1. servlet 包名

Spring MVC 4.x/5.x 使用 `javax.servlet`。

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

### 2. spring-webmvc 版本

主线依赖由父 POM 管理，是 Spring 6。

低版本适配模块需要显式依赖对应版本，例如：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.3.30.RELEASE</version>
</dependency>
```

### 3. HandlerInterceptor 方法

Spring MVC 4.3 的 `HandlerInterceptor` 没有 default method。

适配类需要额外实现：

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

### 4. Controller 注解

当前 `InboundTokenController` 使用的基础注解在 Spring MVC 4.x 可用：

- `@RestController`
- `@RequestMapping`
- `@PostMapping`
- `@RequestBody`
- `@RequestParam`

通常不需要改。

### 5. 建议方式

不建议在主线代码里同时兼容 Spring 4 和 Spring 6。

更合适的方式是后续新增适配模块：

```text
lcxm-basic-third-springmvc4-adapter
```

该模块只放低版本 Web 适配类，核心 service/store/security/outbound 继续复用 `lcxm-basic-third`。

## 复制到 JDK 8 时需要调整

如果不是新建适配模块，而是直接复制当前代码到 JDK 8 环境，至少需要检查这些点：

- `jakarta.servlet` 改为 `javax.servlet`。
- Spring 6 / Boot 3 依赖改为 Spring 4.x/5.x 对应依赖。
- 模式匹配 `instanceof Xxx xxx` 改为普通 `instanceof` 后强转。
- `String.isBlank()` 改为 Hutool `StrUtil.isBlank(...)`。
- `Map.of(...)`、`List.of(...)` 改为 `Collections.singletonMap(...)`、`Arrays.asList(...)` 或手动构造。
- `InputStream.readAllBytes()` 改为循环读取或使用 Hutool/Commons IO。
- `java.time.Duration` 在 JDK 8 可用，但如果低版本项目配置体系不方便绑定，可在适配层改为毫秒数。
- Lombok、Hutool、Caffeine、Jackson 版本需要选择仍支持 JDK 8 的版本。
