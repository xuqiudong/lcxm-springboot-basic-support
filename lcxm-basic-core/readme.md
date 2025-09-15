# 从common 里拆分出来的核心基础模块
第一步：拆分「核心基础模块」lcxm-basic-core（保留通用无依赖内容）
从原 lcxm-common 中 保留 以下代码和依赖，形成 lcxm-basic-core：
1. 保留的代码目录（无框架强依赖）
   util 目录下的核心工具（不依赖 Redis、POI 等）：
   基础工具：StringCommonUtils、DateTimeUtils、JsonUtil、RegexUtil、CommonNumberUtils、ListUtils；
   加密工具：AesUtil、RsaUtils、Digests、Encodes（无第三方依赖）；
   反射工具：ReflectionUtils、CompareClassUtil；
   线程工具：ExecutorPoolUtils、CustomThreadFactory；
   异常工具：ExceptionUtils。
   base 目录下的通用基础（无 ORM、Redis 依赖）：
   通用异常：CommonException、BadParamException、ResourceNotFoundException；
   统一返回体：BaseResponse、BooleanWithMsg；
   通用枚举：CommonMsgEnum、ResultMsg；
   通用模型：BaseEntity（无 MP 注解，仅含 createTime/updateTime）、PageInfo、Remind；
   全局异常处理：web/GlobalExceptionHandler；
   AOP 基础切面：aspect/RequestLogPrintAspect（无专项依赖的日志切面）；
   参数校验工具：tool/HibernateValidatorUtils。

依赖:
```xml
<dependencies>
    <!-- 日志核心 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
    </dependency>
    <!-- 基础工具 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
    </dependency>
    <dependency>
        <groupId>commons-codec</groupId>
        <artifactId>commons-codec</artifactId>
    </dependency>
    <!-- JSON 处理 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    <!-- Spring 基础（AOP、参数校验） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <!-- Servlet API（provided，不打包） -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <scope>provided</scope>
    </dependency>
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```