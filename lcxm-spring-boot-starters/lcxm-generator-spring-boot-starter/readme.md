# lcxm-generator-spring-boot-starter

以 MyBatis-Plus为ORM 的代码生成器，支持生成 Java 后端代码和 Vue3 前端代码（附带的，建议按需自定义模板）。

## 目录

- [快速开始](#快速开始)
- [使用方式](#使用方式)
- [架构设计](#架构设计)
- [配置说明](#配置说明)
- [插件系统](#插件系统)
- [自定义模板](#自定义模板)
- [项目结构](#项目结构)
- [常见问题](#常见问题)

## 快速开始

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-generator-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

## 使用方式

### Facade 模式

```java
public class GeneratorTest {
    public static void main(String[] args) {
        // 创建配置
        CommonFacadeConfig config = CommonFacadeConfig
            .mysql("127.0.0.1", "3306", "mydb", "root", "password")
            .setBasePackage("com.example.project")
            .setModule("system")
            .addTable("sys_user", "sys_role", "sys_menu");
        
        // 生成代码
        Generator generator = CommonGeneratorFacade.build(config);
        generator.generate();
    }
}
```



```java
CommonFacadeConfig config = CommonFacadeConfig
    .mysql("127.0.0.1", "3306", "mydb", "root", "password")
    .setBasePackage("com.example")
    .setModule("demo")
    .setMavenModule("/my-project-module")  // Maven 子模块路径
    .addTable("t_user", "t_order")          // 要生成的表
    .addTablePrefix("t_");                  // 表前缀（自动去除）

// 可选：禁用所有默认模板，只生成 Entity
config.disableAll()
      .addEntityConfig(ec -> ec.disable(false));

Generator generator = CommonGeneratorFacade.build(config);
generator.generate();
```

#### Facade 常用配置

```java
// 1. 自定义作者
config.setAuthor("Vic.xu");

// 2. 自定义输出目录
config.setOutputDir("/path/to/output");

// 3. 启用/禁用注解
config.setLombok(true);       // 默认 true
config.setSpringdoc(true);    // 默认 true
config.setPlus(true);         // 默认 true

// 4. 追加 Entity 配置
config.addEntityConfig(ec -> 
    ec.supperClass(CustomBaseEntity.class)
      .ignoreColumns("temp_field")
);

// 5. 追加 Mapper 配置
config.addMapperConfig(mc -> 
    mc.supperClass(MyBaseMapper.class)
);

// 6. 添加自定义模板（如 Vue 前端）
config.addCustomizeTemplate("vue/apis", "/templates/vue/apis/type.ts")
      .setFileSuffix(".ts")
      .setFileNameFunction(name -> "type.ts");
```

---

### Builder 模式

```java
Generator.create(DatabaseType.mysql, url, username, password)
    // 全局配置
    .globalConfigBuilder(builder ->
        builder.author("Vic.xu")
               .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
               .basePackage("com.example")
               .module("demo")
               .pkType(Long.class)              // 主键类型
               .lombok(true)                    // 启用 Lombok
               .springdoc(true)                 // 启用 Springdoc
               .plus(true)                      // 启用 MyBatis-Plus
               .open(true)                      // 生成后打开目录
               .confirm(true)                   // 覆盖前确认
    )
    
    // 策略配置（核心）
    .strategyConfigBuilder(builder ->
        builder.tables("sys_user", "sys_role")           // 要生成的表
               .tablePrefix("sys_")                      // 表前缀
               .fileOverride(true)                       // 覆盖已有文件
               
               // Entity 配置
               .entityConfig(ec ->
                   ec.supperClass(BaseMpEntity.class)
                     .supperClassWithGeneric(true)       // BaseEntity<Long>
                     .ignoreColumns("temp_field")        // 忽略字段
               )
               
               // Query 配置
               .queryConfig(qc -> qc.disable(false))
               
               // Mapper 配置
               .mapperConfig(mc ->
                   mc.supperClass(StringCrudMapper.class)
                     .supperClassWithGeneric(true)
               )
               
               // XML 配置
               .xmlConfig(xc -> xc.disable(false))
               
               // Service 配置
               .serviceConfig(sc ->
                   sc.supperClassWithGeneric(false)
                     .disable(false)
               )
               
               // Controller 配置
               .controllerConfig(cc ->
                   cc.supperClassWithGeneric(false)
                     .disable(false)
               )
    )
    
    // 自定义模板
    .addCustomizedTemplate(
        CustomizeTemplateConfig.build("customizer", "templates/customizer.java")
            .setDisable(false)
            .setFileSuffix(".java")
            .setFileNameFunction(className -> className + "Customizer")
    )
    
    // 开始生成
    .generate();
```

---

##  架构设计

### 设计理念

```
┌─────────────────────────────────────────────┐
│           用户调用层 (API Layer)             │
│  ┌──────────────┐    ┌──────────────────┐  │
│  │   Facade     │    │    Builder       │  │
│  │  (简化配置)   │    │  (精细控制)       │  │
│  └──────┬───────┘    └────────┬─────────┘  │
└─────────┼─────────────────────┼────────────┘
          │                     │
          └──────────┬──────────┘
                     ▼
┌─────────────────────────────────────────────┐
│          核心引擎层 (Core Engine)             │
│  ┌──────────────────────────────────────┐  │
│  │         Generator (入口)              │  │
│  │  - 收集配置                           │  │
│  │  - 协调生成流程                        │  │
│  └──────────────┬───────────────────────┘  │
│                 │                           │
│  ┌──────────────▼───────────────────────┐  │
│  │      GeneratorFactory (工厂)          │  │
│  │  - 加载配置                           │  │
│  │  - 读取元数据                         │  │
│  │  - 调用插件                           │  │
│  │  - 渲染模板                           │  │
│  │  - 输出文件                           │  │
│  └──────────────┬───────────────────────┘  │
└─────────────────┼──────────────────────────┘
                  │
    ┌─────────────┼─────────────┐
    ▼             ▼             ▼
┌────────┐  ┌──────────┐  ┌──────────┐
│ Plugin │  │ Template │  │   DAO    │
│ 插件层  │  │  模板层   │  │ 数据访问层│
└────────┘  └──────────┘  └──────────┘
```

### 核心组件

#### 1. **Generator（生成器入口）**

负责收集所有配置，提供链式调用 API。

```java
public class Generator {
    private DataSourceConfig.Builder dataSourceConfigBuilder;
    private GlobalConfig.Builder globalConfigBuilder;
    private StrategyConfig.Builder strategyConfigBuilder;
    private BaseTemplateEngine templateEngine;
    private List<IGeneratorPlugin> customizedPlugins;
    private List<CustomizeTemplateConfig> customizedTemplateConfigs;
}
```

#### 2. **ConfigBundle（配置聚合）**

统一管理所有配置项，传递给工厂和插件。

```java
public class ConfigBundle {
    private DataSourceConfig dataSourceConfig;
    private GlobalConfig globalConfig;
    private StrategyConfig strategyConfig;
    private BaseTemplateEngine templateEngine;
    private Set<IGeneratorPlugin> plugins;
    private List<CustomizeTemplateConfig> customizedTemplates;
}
```

#### 3. **GeneratorFactory（生成工厂）**

核心执行引擎，负责完整的生成流程。

**生成流程：**
```
1. confirmGenerate()      → 安全确认（防止误覆盖）
2. listTemplateContexts() → 从数据库读取表元数据
3. beforeGenerate()       → 执行插件前置处理
4. batchOutput()          → 批量渲染模板
   ├─ renderEntity()
   ├─ renderQuery()
   ├─ renderMapperInterface()
   ├─ renderMapperXml()
   ├─ renderService()
   ├─ renderController()
   └─ renderCustomize()
5. open()                 → 打开输出目录
```

#### 4. **Plugin System（插件系统）**

在生成前对上下文数据进行增强。

```java
public interface IGeneratorPlugin {
    boolean enable(ConfigBundle config);           // 是否启用
    void beforeGenerate(TemplateContext context);  // 生成前处理
    void afterGenerate(String content);            // 生成后处理
}
```

**内置插件：**
- `LombokPlugin`：添加 `@Data`、`@Builder` 等注解
- `MybatisPlusPlugin`：添加 `@TableName`、`@TableField` 等注解
- `SpringdocPlugin`：添加 `@Schema` API 文档注解
- `QueryConditionPlugins`：为 Query 类添加查询条件注解
- `LcxmBasicImportPlugin`：自动管理 import 导入

#### 5. **Template Engine（模板引擎）**

默认使用 Freemarker，支持自定义扩展。

```java
public abstract class BaseTemplateEngine {
    public abstract BaseTemplateEngine init(ConfigBundle bundle);
    public abstract String templateSuffix();
    protected abstract void render(String templatePath, Map<String, Object> model, Writer writer);
}
```

---

## 配置说明

### GlobalConfig（全局配置）

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `basePackage` | String | `cn.xuqiudong.generator` | 基础包名 |
| `module` | String | `module` | 模块名（作为子包） |
| `author` | String | `System.getProperty("user.name")` | 作者 |
| `outputDir` | String | `D:\generator` (Win) / `/tmp/generator` (Linux) | 输出目录 |
| `lombok` | boolean | `true` | 启用 Lombok 注解 |
| `springdoc` | boolean | `true` | 启用 Springdoc 注解 |
| `plus` | boolean | `true` | 启用 MyBatis-Plus 注解 |
| `open` | boolean | `true` | 生成后打开目录 |
| `pkType` | Class<?> | `null` | 主键类型（覆盖数据库推断） |
| `confirm` | boolean | `true` | 覆盖前手动确认 |

### StrategyConfig（策略配置）

#### 表配置
- `tables(String... tables)`：要生成的表名
- `tablePrefix(String... prefix)`：表前缀（生成时自动去除）
- `fileOverride(boolean)`：是否覆盖已有文件

#### Entity 配置
```java
.entityConfig(ec ->
    ec.supperClass(BaseMpEntity.class)          // 父类
      .supperClassWithGeneric(true)             // 父类带泛型：<Long>
      .ignoreColumns("field1", "field2")        // 忽略字段
      .disable(false)                           // 是否禁用生成
)
```

#### Mapper 配置
```java
.mapperConfig(mc ->
    mc.supperClass(StringCrudMapper.class)
      .supperClassWithGeneric(true)             // Mapper<User, Long>
      .disable(false)
)
```

#### Service/Controller 配置
```java
.serviceConfig(sc ->
    sc.supperClassWithGeneric(false)            // 不支持泛型
      .disable(false)
)

.controllerConfig(cc ->
    cc.supperClassWithGeneric(false)
      .disable(false)
)
```

---

## 插件系统



### 自定义插件示例

```java
public class CustomPlugin implements IGeneratorPlugin {
    
    @Override
    public boolean enable(ConfigBundle config) {
        return true; // 根据配置决定是否启用
    }
    
    @Override
    public void beforeGenerate(TemplateContext context) {
        // 在 Entity 上添加自定义注解
        context.getEntity().addImport("com.example.CustomAnnotation");
        context.getEntity().addClassAnnotation("@CustomAnnotation");
    }
    
    @Override
    public void afterGenerate(String content) {
        // 后处理生成的内容（如格式化）
    }
}

// 使用
Generator.create(...)
    .addPlugin(new CustomPlugin())
    .generate();
```

---

##  自定义模板

### 场景 1：生成 Vue3 前端代码

```java
// 生成 type.ts（TypeScript 类型定义）
CustomizeTemplateConfig typeTsConfig = CustomizeTemplateConfig
    .buildNotJavaTemplate("vue/apis", "/templates/vue/apis/type.ts")
    .setFileSuffix(".ts")
    .setFileNameFunction(name -> "type.ts");

// 生成 index.ts（API 接口）
CustomizeTemplateConfig indexTsConfig = CustomizeTemplateConfig
    .buildNotJavaTemplate("vue/apis", "/templates/vue/apis/index.ts")
    .setFileSuffix(".ts")
    .setFileNameFunction(name -> "index.ts");

// 生成 index.vue（页面组件）
CustomizeTemplateConfig vueConfig = CustomizeTemplateConfig
    .buildNotJavaTemplate("vue", "/templates/vue/index.vue")
    .setFileSuffix(".vue")
    .setFileNameFunction(name -> "index.vue");

config.addCustomizeTemplate(typeTsConfig, indexTsConfig, vueConfig);
```

### 场景 2：生成自定义 Java 类

```java
CustomizeTemplateConfig customConfig = CustomizeTemplateConfig
    .build("dto", "templates/custom/dto.java")
    .setFileSuffix(".java")
    .setFileNameFunction(className -> className + "DTO")
    .setDisable(false);

config.addCustomizeTemplate(customConfig);
```

### 模板变量

所有模板都可以访问 `TemplateContext` 中的数据：

```freemarker
<#-- entity.java.ftl 示例 -->
package ${entity.packageName};

import lombok.Data;

/**
 * ${table.comment}
 * @author ${author}
 * @since ${date}
 */
@Data
public class ${entity.className} extends ${entity.supperClass} {
<#list entity.fields as field>
    /**
     * ${field.comment}
     */
    private ${field.javaType} ${field.fieldName};
</#list>
}
```

**可用变量：**
- `author`：作者
- `date`：生成日期
- `table`：表信息（`TableMeta`）
- `entity`：实体上下文（`EntityContext`）
- `mapper`：Mapper 上下文
- `service`：Service 上下文
- `controller`：Controller 上下文
- `query`：Query 上下文

---

## 项目结构

```
lcxm-generator-spring-boot-starter/
├── src/
│   ├── main/
│   │   ├── java/cn/xuqiudong/basic/generator/
│   │   │   ├── autoconfigure/          # Spring Boot 自动配置（Web 模式）
│   │   │   ├── config/                 # 配置类
│   │   │   │   ├── DataSourceConfig.java
│   │   │   │   ├── GlobalConfig.java
│   │   │   │   ├── StrategyConfig.java
│   │   │   │   └── template/           # 各模板配置
│   │   │   │       ├── EntityTemplateConfig.java
│   │   │   │       ├── MapperTemplateConfig.java
│   │   │   │       └── ...
│   │   │   ├── constant/               # 常量定义
│   │   │   ├── dao/                    # 数据访问层（多数据库支持）
│   │   │   │   ├── BaseGeneratorDao.java
│   │   │   │   ├── MysqlGeneratorDao.java
│   │   │   │   ├── OracleGeneratorDao.java
│   │   │   │   └── GaussGeneratorDao.java
│   │   │   ├── dialect/                # 数据库方言（关键字处理）
│   │   │   ├── engine/                 # 模板引擎
│   │   │   │   ├── BaseTemplateEngine.java
│   │   │   │   └── FreemarkerTemplateEngine.java
│   │   │   ├── enums/                  # 枚举
│   │   │   │   ├── DatabaseType.java
│   │   │   │   └── TemplateType.java
│   │   │   ├── factory/                # 工厂类
│   │   │   │   ├── GeneratorFactory.java
│   │   │   │   └── DataAssemblyFactory.java
│   │   │   ├── model/                  # 数据模型
│   │   │   │   ├── context/            # 上下文对象
│   │   │   │   │   ├── TemplateContext.java
│   │   │   │   │   ├── EntityContext.java
│   │   │   │   │   └── ...
│   │   │   │   ├── meta/               # 元数据
│   │   │   │   │   ├── TableMeta.java
│   │   │   │   │   └── ColumnMeta.java
│   │   │   │   └── query/              # 查询对象
│   │   │   ├── plugin/                 # 插件系统
│   │   │   │   ├── IGeneratorPlugin.java
│   │   │   │   ├── BaseGeneratorPlugin.java
│   │   │   │   └── impl/               # 内置插件
│   │   │   │       ├── LombokPlugin.java
│   │   │   │       ├── MybatisPlusPlugin.java
│   │   │   │       ├── SpringdocPlugin.java
│   │   │   │       └── ...
│   │   │   ├── registry/               # 注册表（SPI）
│   │   │   │   ├── DataTypeMappingRegistry.java
│   │   │   │   ├── GeneratorDaoProvider.java
│   │   │   │   └── KeyWordsHandlerRegistry.java
│   │   │   ├── util/                   # 工具类
│   │   │   │   ├── NameConvertUtils.java
│   │   │   │   ├── TypeConvertUtil.java
│   │   │   │   └── ImportPackageUtils.java
│   │   │   ├── CommonFacadeConfig.java     # Facade 配置
│   │   │   ├── CommonGeneratorFacade.java  # Facade 门面
│   │   │   └── Generator.java              # 生成器入口
│   │   └── resources/templates/        # 模板文件
│   │       ├── entity.java.ftl
│   │       ├── mapper.java.ftl
│   │       ├── mapper.xml.ftl
│   │       ├── service.java.ftl
│   │       ├── controller.java.ftl
│   │       ├── query.java.ftl
│   │       └── vue/                    # Vue 前端模板
│   │           ├── apis/
│   │           │   ├── type.ts.ftl
│   │           │   └── index.ts.ftl
│   │           └── index.vue.ftl
│   └── test/java/
│       └── cn/xuqiudong/generator/
│           ├── MainGeneratorTest.java      # 测试用例
│           └── TestGeneratorApplication.java
├── front/                              # 前端项目（Web 模式 UI）
│   ├── src/
│   │   ├── utils/
│   │   │   ├── request.ts
│   │   │   └── service.ts
│   │   ├── App.vue
│   │   └── main.ts
│   ├── package.json
│   └── vite.config.ts
├── pom.xml
└── README.md
```

---

## 常见问题

### 如何只生成 Entity？

```java
config.disableAll()  // 禁用所有
      .addEntityConfig(ec -> ec.disable(false));  // 只启用 Entity
```

### 如何自定义 Entity 父类？

```java
// Facade 模式
config.addEntityConfig(ec -> 
    ec.supperClass(MyBaseEntity.class)
      .supperClassWithGeneric(false)
);

// Builder 模式
.entityConfig(ec -> 
    ec.supperClass(MyBaseEntity.class)
)
```

### 如何去除表前缀？

```java
config.addTablePrefix("t_", "sys_");
// t_user → User
// sys_role → Role
```

### 生成的代码在哪里？

默认输出路径：
- Windows: `D:\generator`
- Linux: `/tmp/generator`

可通过 `setOutputDir()` 自定义：
```java
config.setOutputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java");
```

### 如何避免覆盖已有代码？

```java
// 方式1：设置不覆盖
config.setFileOverride(false);  // 默认 false

// 方式2：启用确认提示
config.setConfirm(true);  // 默认 true，main 方法运行时会提示 yes/no
```

### 支持哪些数据库？

当前支持：
- MySQL
- Oracle
- GaussDB

扩展新数据库：
```java
public class PostgresGeneratorDao extends BaseGeneratorDao {
    // 实现数据库特定的元数据查询
}

// 注册到 SPI
GeneratorDaoProvider.register(DatabaseType.postgresql, PostgresGeneratorDao::new);
```

### 如何调试模板？

1. 查看 `TemplateContext` 的结构
2. 在模板中添加调试输出：
```freemarker
<#-- 打印所有变量 -->
${.data_model?keys}

<#-- 打印 Entity 信息 -->
${entity.className}
${entity.fields?size}
```

### Web 模式如何使用？

项目中包含 `front/` 目录，是一个 Vue3 前端项目，提供可视化的代码生成界面。

启动步骤：
```bash
cd front
pnpm install
pnpm dev
```

访问 `http://localhost:5173`，配置数据源后即可在线生成代码。

---

## 开发指南

### 添加新的模板类型

1. 创建 Context 类：
```java
public class DtoContext extends BaseContext {
    private List<FieldInfo> fields;
    // getters/setters
}
```

2. 创建配置类：
```java
public class DtoTemplateConfig extends BaseTemplateConfig {
    public static class Builder {
        // 配置项
    }
}
```

3. 在 `GeneratorFactory` 中添加渲染方法：
```java
private void renderDto(TemplateContext context) {
    render(TemplateType.DTO, context, 
           bundle.getStrategyConfig().getDtoTemplateConfig(), 
           context.getDto());
}
```

4. 创建模板文件 `dto.java.ftl`

### 添加新的插件

参考 `LombokPlugin.java` 的实现，实现 `IGeneratorPlugin` 接口即可。

---

## 最佳实践

### 1. 统一主键类型

建议在项目中统一主键类型（Long 或 String）：

```java
config.setPkType(Long.class);  // 所有表的主键都是 Long
```

### 2. 合理使用父类

创建项目基类，减少重复代码：

```java
// 自定义 Entity 父类
public class MyBaseEntity extends BaseMpEntity<Long> {
    // 添加项目通用字段
}

// 配置
config.addEntityConfig(ec -> 
    ec.supperClass(MyBaseEntity.class)
      .supperClassWithGeneric(false)
);
```

### 3. 版本控制



### 4. 自定义业务逻辑

生成的 Service/Controller 只包含基础 CRUD，业务逻辑应：
- 在 Service 实现类中扩展
- 或创建新的 Service 方法

---

