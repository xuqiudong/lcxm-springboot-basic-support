# 代码生成器starter 主要是mybatis-plus的Entity

- 支持springboot web方式集成, 依赖于框架中注入的JdbcTemplate
- 支持main函数方式, 需要手动配置数据源

> 核心是生成Entity:

## 全局设置:

- 作者 author
- 包 package
- 模块 module
- 输出路径:  如果是main函数方式生成的话
    - 子包:  entity, mapper, service, service, controller
- 数据库类型: database
- 表前缀
- 启用扩展插件:
    - 是否启用swagger
    - 是否启用lombok
- 数据类型转换 数据库类型--> java 类型
    - 会默认一些 JdbcType 到Java类的映射, 但是也支持额外配置
- 生成的文件类型 OutputFile: 包括 entity, mapper, mapper.xml, service, controller
- 主键字段类型: 配置或者从数据库获取(用于定义主键泛型)

## Entity生成的

- 父类, 父类中所有字段 superEntityColumns
    - 一般父类中已经包含了 id, 创建人, 创建时间, 修改人, 修改时间, 版本信息, 逻辑删除字段等
- 忽略的表字段 ignoreColumns
- 插件设置:
    - lombok插件: 字段注解,  (类注释直接根据启用与否 在模板内加)
    - swagger插件:字段注解,  (类注释直接根据启用与否 在模板内加)
- import 导包: 根据字段类型, 添加对应的import
- 标注字段类型是否是大字段, 生成的时候标注select=false
- 主键类型: 暂时只判断 mysql的 auto_increment 为自增主键, 其他的 使用默认的 IdType.ASSIGN_UUID
- 父Entity是否指定泛型(默认false):  如果指定了泛型, 则泛型跟随主键 比如 User extends BaseEntity<Long>

## mapper 和 mapper.xml 生成
- mapper:
  -  默认父类: mybatis-plus的 BaseMapper<T> ,如果修改父类, 父类依然只保持一个泛型<T>: 即Entity
    - 如果父Mapper, 需要额外支持一个Id 的泛型,  可以中间继承一个接口, 理论上来说一个项目中的主键 应该是统一的
- mybatis-plus 的 mapper基本操作放在 封装好的父MpMapper中, 其可能继承mybatis-plus的BaseMapper
- 但是如果指定生成xml, 则多继承一个父Mapper(内置一些增删改查方法), 然后生成和父Mapper对应的mapper.xml
- 主要是结合父Mapper, 指定好主键泛型和实体泛型
- mapper xml: 是否要把默认的CRUD 写在里面 是不是会和mybatis-plus的BaseMapper 冲突
  - 生成resultMapper: 
    - 基础 BaseResultMap 仅含主键
    - BaseListResultMap 继承父ResultMap: list查询, 不包含lob字段
    - DetailListResultMap 继承父BaseListResultMap: 详情查询, 全部字段
  -  转义的字符串:  转义字符串, 避免xml解析错误; 但是原始字符串也需要,因为ResultMap 使用原始字符串

## service 生成

- 可以定义好父service, 把相关方法写在父方法中, 然后继承父service, 指定好实体泛型
- 此处纠结要不要父service 还是直接写在在当前service中定义方法

## controller生成

- 主要考虑加上swagger相关注解
- 内置一些常见的增删改查方法
- 此处也纠结要不要父controller, 还是直接写在在当前controller中定义方法, 用父controller也只能定义一些默认方法,
  而不应该定义@RequestMapping

## 上下文数据模型 重构

- TemplateContext
    - 一些全局的基本信息: 作者, 时间, 插件开关
    - TableInfo:  表数据 一般对应 mapper.xml 模板
    - EntityContext: 实体信息: 一般用于 entity.java 模板

## 文件路径构件

- parent + module + subPackage + fileName : 输出到前端,则以这样的形式组织在zip中
- 输出到本地的, 则上面的路径基础上加上全局配置GlobalConfig里指定的 outputDir, outputDir默认路径:
    - windows: D:\\generator
    - linux: /tmp/generator";

## other
- 导入 默认字典排序
todo :   表前缀 去除