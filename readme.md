# lcxm-springboot-basic support
> start 2025-05-26

## 升级背景
把原来的一些[基础支持](https://gitee.com/basic-support)合并到本项目，方便管理,并升级jdk和springboot版本
   - parent:[lcxm-springboot-parent](https://gitee.com/basic-support/lcxm-springboot-parent)
   - base-common:[lcxm-common](https://gitee.com/basic-support/lcxm-common)
   - 一些starter
      - [lcxm-generate-spring-boot-starter](https://gitee.com/basic-support/lcxm-generate-spring-boot-starter)
      - [starter-quartz](https://gitee.com/basic-support/starter-quartz)
      - [lcxm-mq-data-bridge-spring-boot-starter](https://gitee.com/basic-support/lcxm-mq-data-bridge-spring-boot-starter)

真是合久必分，分久必合。

## 升级目标
1. jdk8 先升级到 jdk 17
2. springboot2.7.3 升级到3.x
3. jdk17考虑从升级到 jdk21

## 升级后版本说明
**Spring主版本-JDK版本-项目版本**: 形如 3.1.2-jdk17-1.0.1

版本管理策略: 增量更新
- 仅修复Bug → 递增PATCH（如3.1.2-jdk17-1.0.1） 
- 新增功能 → 递增MINOR（如3.1.2-jdk17-1.1.0）
- 不兼容变更 → 递增MAJOR（如4.0.0-jdk21-1.0.0）

考虑使用 使用maven-release-plugin自动更新版本号并打Tag

## 升级步骤：

### 1 本地maven软件升级:
 [Download Apache Maven – Maven](https://maven.apache.org/download.cgi?.)
 - 本地jdk 切换为17
 - maven软件从3.6.3 升级到 3.9.9

### 2 项目迁移合并: 只修改项目结构
 - 新建本项目
 - 迁移原parent 作为父模块
 - 迁移 lcxm-common 为子模块
 - 迁移 lcxm-generate-spring-boot-starter 为子模块
 - 迁移 lcxm-mq-data-bridge-spring-boot-starter 为子模块
 - 迁移 lcxm-quartz-spring-boot-starter  为子模块

### 3  jdk8 升级到 jdk17
- 修改版版本号： 
  - `mvn versions:set -DnewVersion="2.7.3-jdk17-1.0.0" -DprocessAllModules=true  -DgenerateBackupPoms=false`
- parent 的 pom.xml中的properties定义的jdk版本修改为17
- idea 中项目、模块、编译等设置为jdk17
- 编译：`mvn clean compile`
- 依赖树: `mvn dependency:tree -Dverbose`
- 检测下各个模块是否使用jdk内部过时api
  - 进入 各个子模块： `jdeps -jdkinternals target/classes`

### 4 springboot2.7.3 升级到 3.5.0 ★★★
> 2025-05-27

### 依赖变化
- mysql:mysql-connector-java 修改为 com.mysql:mysql-connector-j
- org.glassfish:jakarta.el  版本号不再由springboot管理
  - 修改为 org.springframework.boot:spring-boot-starter-validation,  包含
     - hibernate-validator（实现）
     - jakarta.validation-api（规范）
- javax.* → jakarta.* 迁移
  - javax.servlet:javax.servlet-api:4.0.0 迁移为 jakarta.servlet:jakarta.servlet-api:6.1.0
  - javax.validation:validation-api:2.0.1.Final 移除
    - 因为 org.springframework.boot:spring-boot-starter-validation 已经引入 jakarta.validation:jakarta.validation-api:3.1.1
- org.apache.shiro:shiro-spring:1.7.1 移除，因2.x版本依然不支持 Jakarta EE，额外需要依赖

- mybatis-spring-boot-starter 版本 2.2.2 升级到 3.0.4
- pagehelper-spring-boot-starter 版本 1.4.2 升级到 2.1.0
- common-lang3  版本 3.12.0 升级到3.17.0
- poi 版本 4.1.2 升级到  5.4.1 
  - poi-ooxml-schemas 修改为 poi-ooxml-lite(精简版 poi-ooxml-full 完整版)
-  org.springdoc:springdoc-openapi-ui:1.8.0 升级到 org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8
- org.redisson:redisson-spring-boot-starter:3.17.7 升级到 3.47.0
- com.github.ben-manes.caffeine:caffeine:2.9.3 升级到3.2.0
- 删除 elasticsearch 的版本重新定义, 保持springboot的对elasticsearch的版本管理
- 删除 jsqlparser 依赖，交由 pagehelper 传递而来

#### 因依赖导致的代码变化
- CommonsMultipartResolver 修改为 StandardServletMultipartResolver
- javax.* 修改为  jakarta.* ; 但是如下的部分不修改
  - javax.net.ssl.* 
  - javax.imageio.*
  - javax.crypto.*
- redis.clients.jedis.Tuple; 修改为 redis.clients.jedis.resps.Tuple;
- ListenableFuture(Spring Framework) 修改为 CompletableFuture(JDK 标准库)。

#### logback相关变化
从 Logback 1.3.0 开始，官方推荐直接在 <logger> 或 <root> 标签中使用 level 属性来指定日志级别，而不是独立的 <level> 子元素

#### springboot相关变化
- 自定义 Starter **自动装配类**（之前就已经完成）
  -  META-INF/spring.factories 为 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

#### 配置文件调整
- 添加 spring-boot-properties-migrator 生成迁移报告
> 启动时扫描环境变量和配置文件,生成迁移诊断报告 打印警告日志并临时映射旧属性到新属性
> 查看控制台输出的属性迁移建议。
> 根据日志修改配置
> 完成迁移后删除该模块
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-properties-migrator</artifactId>
    <scope>runtime</scope>
</dependency>
```
- Redis 配置：spring.redis → spring.data.redis



>  2025-05-29测试推送到gitea，触发gitea推送到gitee