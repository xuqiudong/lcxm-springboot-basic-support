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
- parent 的 pom.xml中的properties定义的jdk版本修改为17
- idea 中项目、模块、编译等设置为jdk17
- 编译：`mvn clean compile`
- 依赖树: `mvn dependency:tree -Dverbose`
- 检测下各个模块是否使用jdk内部过时api
  - 进入 各个子模块： `jdeps -jdkinternals target/classes`