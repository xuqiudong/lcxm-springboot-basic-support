## SrpcReference 注解 RPC 框架改造说明 

> 202603

###  1 改造原因
#### 1.1 支持字段级差异化配置

原有实现基于 Spring Bean 注入，每个接口类型只生成一个 BeanDefinition。
当不同字段需要不同 RPC 配置（app_code、timeout 等）时，单 Bean 无法满足需求。

#### 1.2 简化 Spring Bean 管理复杂度

为每个字段生成不同 BeanDefinition 会导致 Bean 数量膨胀，管理复杂。
部分代理类无需依赖 Spring 生命周期或其他 Bean，可以轻量化处理。

#### 1.3 保持框架兼容性

已有大量业务代码使用 @SrpcReference 注解。
改造后不希望修改业务层代码，只调整内部注入逻辑。

### 2 改造内容
#### 2.1 时机调整

改造前：BeanFactoryPostProcessor → 在 Bean 定义阶段注册 XqdBeanFactory BeanDefinition。
改造后：BeanPostProcessor → 在 Bean 实例化后，初始化前扫描字段并注入代理。

#### 2.2 代理生成方式调整

改造前：Spring 管理 XqdBeanFactory Bean，注入通过 @Autowired 自动完成。

改造后：每个字段手动创建 XqdBeanFactory，调用 getObject() 获取代理，通过 反射注入字段。

#### 2.3 支持字段级别配置差异

使用注解参数（app_code、timeout 等）生成独立代理对象。

可选缓存：相同接口+相同配置的字段共享代理对象，避免重复创建。

保持现有业务代码不变

注解写法保持 @SrpcReference

字段声明保持原样，无需添加 @Qualifier 或修改类型

### 3  改造前后对比
| 对比维度           | 改造前                                               | 改造后                                        |
| ------------------ | ---------------------------------------------------- | --------------------------------------------- |
| 注入方式           | Spring 自动注入 Bean                                 | 反射注入字段                                  |
| Bean 生命周期管理  | Spring 管理 `XqdBeanFactory` Bean                    | 不再由 Spring 管理，代理对象由工厂生成并注入  |
| 时机               | `BeanFactoryPostProcessor` → BeanDefinition 注册阶段 | `BeanPostProcessor` → Bean 实例化后、初始化前 |
| 支持字段差异化配置 | 不支持，单接口单 Bean                                | 支持，每个字段可根据注解生成独立代理          |
| Spring Bean 数量   | 每个接口生成一个 BeanDefinition                      | 不生成额外 Bean，轻量化处理                   |
| 兼容性             | 原有代码依赖 Spring 注入                             | 保持现有注解和字段写法完全兼容                |

### 4  改造效果

- 每个 @SrpcReference 字段都可有独立的 RPC 配置

- 现有业务代码无需改动

- 框架更轻量、灵活

- 可选缓存复用相同配置代理对象，节省资源