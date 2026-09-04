# lcxm-basic-mybatis-plus

MyBatis-Plus 通用增强模块。

`lcxm-basic-mybatis-plus` 面向使用 MyBatis-Plus 的项目，提供自动配置、通用 Mapper/Service、查询条件构建、字段级数据权限、实体自动填充、SQL 注入扩展等能力。

不使用 MyBatis-Plus 的项目不应依赖本模块。

## 主包 Tree

```text
cn.xuqiudong.basic.mybatisplus
|-- annotation             # 查询条件、通用模型标记等注解
|-- autoconfigure          # MyBatis-Plus 自动配置和 lcxm.mp 配置入口
|-- builder                # QueryWrapper 构建器
|-- cache                  # MyBatis-Plus 场景缓存辅助
|-- controller             # 通用 Controller 基类
|-- convert                # 查询/模型转换辅助
|-- entity                 # 带 MP 注解的实体基类
|-- enums                  # 查询操作、排序等枚举
|-- fill                   # MetaObjectHandler 组合和实体自动填充
|-- function               # Wrapper 处理函数接口
|-- helper                 # Mapper 反射、SQL 执行辅助
|-- injector               # 自定义 SQL 注入器
|-- interceptor            # MyBatis 拦截器扩展
|-- mapper                 # 通用 Mapper、ID 类型 Mapper、XML SQL
|-- model                  # 查询、排序、分页等模型
|-- permission             # 字段级数据权限上下文、注解、MP Handler、AOP
|-- query                  # 查询对象基类
|-- service                # 通用 Service 基类
`-- util                   # Wrapper、列名、数据库类型等工具
```

## 主要能力

| 能力 | 说明 |
| --- | --- |
| 自动配置 | `LcxmMybatisPlusAutoConfiguration` 注册分页、数据权限、自动填充、SQL 注入器。 |
| 通用 Mapper | `MpGenericMapper` 增加常用查询、首条查询、批量、字段检查等方法。 |
| 通用 Service | `BaseGenericService`、`BaseService` 提供基础 CRUD 支撑。 |
| 查询构建 | `MpQuery` + `@QueryCondition` + `WrapperBuilder` 构建 `QueryWrapper`。 |
| 字段数据权限 | 基于 MyBatis-Plus `DataPermissionInterceptor` 追加权限 SQL。 |
| 实体自动填充 | 通过 `CompositeAutoFillFieldHandler` 聚合多个填充处理器。 |
| SQL 注入扩展 | `LcxmDefaultSqlInjector` 扩展 MyBatis-Plus 默认方法集合。 |

## Maven

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-mybatis-plus</artifactId>
    <version>4.0.0</version>
</dependency>
```

## 配置

模块总开关：

```properties
lcxm.mp.enabled=true
```

默认启用。

## 启动流程

```text
Spring Boot
  -> AutoConfiguration.imports
  -> LcxmMybatisPlusAutoConfiguration
      -> BaseMpEntityAutoFillFieldHandler
      -> CompositeAutoFillFieldHandler
      -> DataPermissionInterceptor
      -> MybatisPlusInterceptor
          -> DataPermissionInterceptor
          -> 其他 InnerInterceptor
          -> PaginationInnerInterceptor
      -> LcxmDefaultSqlInjector
```

## 字段级数据权限

字段级数据权限用于在 Mapper 查询 SQL 上追加权限条件。

流程：

```text
业务代码
  -> RowDataHelper.start(...) 或 @RowDataPermission
  -> RowDataPermissionAspect 写入权限上下文
  -> MyBatis-Plus DataPermissionInterceptor
  -> MPDataPermissionHandler 读取权限上下文
  -> RowDataHandlerType 生成权限 SQL
  -> JSQLParser 拼接到原 SQL
```

### 手动启用

适合 Service 内部的灵活场景。开启后必须在 `finally` 中清理上下文。

```java
try {
    RowDataHelper.start(TestRowDataHandlerType.AGE_GT_18, "age", "a.name is not null");
    RowDataHelper.start(TestRowDataHandlerType.ID_EQ_2, "id");
    return baseMapper.selectList(wrapper);
} finally {
    RowDataHelper.clear();
}
```

### 注解启用

适合 Mapper 方法上的固定权限场景。

```java
@RowDataPermission({
        @RowDataPermission.Item(column = "age", type = TestRowDataHandlerType.class, value = "AGE_GT_18"),
        @RowDataPermission.Item(column = "id", type = TestRowDataHandlerType.class, value = "ID_GT_2")
})
List<Employee> customerSelect(@Param("note") String note);
```

### 权限类型

业务项目实现 `RowDataHandlerType`，返回可被 JSQLParser 解析的条件 SQL。

```java
public enum TestRowDataHandlerType implements RowDataHandlerType {

    ID_EQ_1 {
        @Override
        public String handlerSql(String column) {
            return column + " = 1";
        }
    }
}
```

## 查询对象

```text
DTO extends MpQuery
  -> 字段标注 @QueryCondition
  -> QueryConditionUtils
  -> WrapperBuilder
  -> QueryWrapper
  -> MpGenericMapper 查询
```

适合将接口查询参数稳定转换为 MyBatis-Plus 查询条件。复杂 SQL 仍建议在 Mapper XML 中显式编写。

## 依赖边界

适合放入本模块：

- MyBatis-Plus 通用 Mapper / Service / Wrapper 能力。
- MyBatis-Plus 插件、拦截器、SQL 注入器。
- 与数据库查询模型直接相关的基础对象。
- 可被多个项目复用的数据权限、自动填充能力。

不适合放入本模块：

- 具体业务表、业务 Mapper、业务权限规则。
- Excel、第三方对接、MQ、Quartz、代码生成器等专项能力。
- 与 MyBatis-Plus 无关的 Spring Web 通用能力。

## 注意点

- `RowDataHelper` 使用线程上下文，手动启用时必须清理。
- 权限 SQL 片段必须是合法条件表达式，不要拼接完整 `where`。
- 分页插件应放在 MyBatis-Plus 拦截器链最后，当前自动配置已处理。
- 业务项目如自定义 `MybatisPlusInterceptor`、`PaginationInnerInterceptor`、`DefaultSqlInjector`，会覆盖默认 Bean。
