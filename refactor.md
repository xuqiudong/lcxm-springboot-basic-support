## 项目结构重构

### lcxm-basic-core
从common 里拆分出来的核心基础模块
「核心基础模块」lcxm-basic-core（保留通用无依赖内容）
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

### lcxm-basic-mybatis-plus
mybatis-plus 模块
- 公共字段填充
- 基于字段的权限拦截 RowDataHelper
- MpGenericMapper  实现了一些默认的方法
- 基于 自定义注解QueryCondition 的查询条件封装

### lcxm-generate-spring-boot-starter → lcxm-generator-spring-boot-starter
原来基于velocity 模板生成的  mybatis 的 增删改查， 主要是实体和xml
现在是基于freemarker末班生成的mybatis-plus 的 增删改查， 主要是实体， 依赖 lcxm-basic-mybatis-plus 模板