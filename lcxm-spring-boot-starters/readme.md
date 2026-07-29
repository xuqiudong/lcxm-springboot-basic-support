# lcxm-spring-boot-starters

Spring Boot starter 聚合模块。

本模块只聚合当前工程内的 starter 子模块，`packaging=pom`，自身不作为业务项目依赖入口，也不发布 deploy。

## 子模块

```text
lcxm-spring-boot-starters
|-- lcxm-generator-spring-boot-starter        # 代码生成器 starter
|-- lcxm-mq-data-bridge-spring-boot-starter   # MQ 数据桥接 starter
`-- lcxm-quartz-spring-boot-starter           # Quartz 定时任务 starter
```

## Maven

业务项目按需依赖具体 starter：

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-quartz-spring-boot-starter</artifactId>
    <version>3.5.0-jdk21-3.0.0</version>
</dependency>
```

## 构建

构建全部 starter：

```bash
mvn -pl lcxm-spring-boot-starters -am clean install
```

构建单个 starter：

```bash
mvn -pl lcxm-spring-boot-starters/lcxm-quartz-spring-boot-starter -am clean install
```

## 依赖边界

适合放入 starter：

- 需要自动装配的专项能力。
- 依赖较重、但业务项目可按需选择的能力。
- 面向 Spring Boot 接入的一站式配置。

不适合放入 starter：

- 基础工具类。
- 不依赖 Spring Boot 自动配置的普通能力。
- 具体业务项目配置和业务逻辑。

## 文档

| Starter | 文档 |
| --- | --- |
| Quartz | [lcxm-quartz-spring-boot-starter/readme.md](lcxm-quartz-spring-boot-starter/readme.md) |
| Generator | [lcxm-generator-spring-boot-starter/readme.md](lcxm-generator-spring-boot-starter/readme.md) |
| MQ Data Bridge | [lcxm-mq-data-bridge-spring-boot-starter/readme.md](lcxm-mq-data-bridge-spring-boot-starter/readme.md) |
