## 版本说明
- 本意是想采用类似 spring cloud 的版本号：**年份.季度.修订号[可选].版本阶段[可选]** 
- 后放弃,决定使用`x.y.z-year` 的方式,即在原来的x.y.z 版本好后面加上年份
### maven坐标
[Maven Central: 搜索本项目](https://central.sonatype.com/search?q=cn.xuqiudong.basic)
```
<dependency>
    <groupId>cn.xuqiudong.common</groupId>
    <artifactId>lcxm-generate-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```
### 1.1.0-2025  2025-03-28
- 2025-03-28 
  - [refactor] 测试启动类移动到test下，包括启动类和application.properties
  - [feat] entity/mapper/service/controller 分别修改为继承BaseGenericXxx  ★★
  - [feat] 当表没设置主键的时候，加入主键推断，根据字段名称推断
  - [fix] 修复entity中的无效引入: 判断非基类中的字段类型才引入

### 1.0.5-2025
- 2025-01-24
- xml等一些格式化
- 打包的时候移除启动类和application.properties

### 1.0.4-2024
- 2024-10-22
- model中import 日期和BigDecimal的判断


### 1.0.3-2024
- 2024-10-22
- lcxm-common.version升级为1.0.4-2024
- mapper.xml中的base list中展示的字段只判断是否大文本


### 1.0.2-2024
- 2024-06-05
- 版本管理继承`lcxm-springboot-parent`

### 1.0.1-2024
- 2024-05-22 首次发布 
- 依赖于cn.xuqiudong.basic/lcxm-common@1.0.1
- 初始提交，来源于[lcxm-generate-starter](https://gitee.com/xuqiudong/boot-support/tree/master/module-starter/lcxm-generate-starter)：

