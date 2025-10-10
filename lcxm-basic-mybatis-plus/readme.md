# mybatis plus 模块

## 基于mybatis-plus的权限拦截器: 基于字段的权限控制
- 支持mapper接口内方法上加RowDataPermission注解的方式（切面实现）
- 支持手动通过RowDataHelper.start开启，需要手动调用clear()方法结束
- 最终的权限sql均在RowDataHandlerType（子类枚举中实现）， 框架只是组装权限sql


**1 定义权限拦截器:**

> 注册一个 DataPermissionInterceptor,使用 MPDataPermissionHandler 作为DataPermissionHandler,
> 然后把 DataPermissionInterceptor 注册到 MybatisPlusInterceptor 中

```java
@Bean
public DataPermissionInterceptor dataPermissionInterceptor() {
      DataPermissionInterceptor interceptor = new DataPermissionInterceptor();
      interceptor.setDataPermissionHandler(new MPDataPermissionHandler());
      return interceptor;
   }
```

**2. 定义mapper接口的切面处理类**

> 支持在mapper接口的方法上添加注解, 启用权限拦截

```java
/**
 * 数据权限 AOP , 自动为 MPDataPermissionHandler 注入权限数据
 */
@Bean
public RowDataPermissionAspect rowDataPermissionAspect() {
    return new RowDataPermissionAspect();
}
```

**3. 使用方式**
3.1 手动启用: 可以在service中手动启用: 适合灵活场景

> 在mapper查询之前开启针对字段的权限拦截, 支持多种组合, 多个条件之间是OR关系, 第三个参数为前置条件
> 最终形成的 where 追加的为  and ( 权限sql2 OR  权限sql2 OR (权限sql3的前置条件 AND 权限sql3))
> 注意, 启用后需要在finally中调用clear()方法

```java
 public List<Employee> listByCondition(LambdaQueryWrapper<Employee> wrapper ) {
    try {
        RowDataHelper.start(TestRowDataHandlerType.AGE_GT_18, "age", "a.name is not null");
        RowDataHelper.start(TestRowDataHandlerType.AGE_LT_35, "age");
        RowDataHelper.start(TestRowDataHandlerType.ID_EQ_2, "id");
        return baseMapper.selectList(wrapper);
    } finally {
        RowDataHelper.clear();
    }
}
```

3.2 在Mapper接口方法上中通过注解方式启用: 适合固定场景

> 其中 type实现 RowDataHandlerType 方法的枚举,  value 为此枚举的字面量, precondition 为前置条件

```java
@RowDataPermission({
        @RowDataPermission.Item(column = "age", type = TestRowDataHandlerType.class, value = "AGE_GT_18", precondition = "a.age < 42"),
        @RowDataPermission.Item(column = "id", type = TestRowDataHandlerType.class, value = "ID_GT_2")
})
List<Employee> customerSelect(@Param("note") String note);
```

**4.自定义枚举: 字段拦截的核心逻辑**

> 实现 RowDataHandlerType 接口, 在枚举内 重写handlerSql方法, 返回一个权限sql片段 : 必须满足 CCJSqlParserUtil.parseCondExpression ,
> 符合基本 SQL 语法（尤其是引号、括号、关键字使用）

```java
public enum TestRowDataHandlerType implements RowDataHandlerType {

    ID_EQ_1 {
        @Override
        public String handlerSql(String column) {
            return column + " = 1";
        }
    },
}
```