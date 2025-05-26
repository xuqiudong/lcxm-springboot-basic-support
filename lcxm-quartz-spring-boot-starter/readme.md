## quartz  starter
> 2025-01

> 虽然对quartz不甚熟悉，但是几年前还是写了个starter 在项目中使用，也没什么太大的问题，这次准备进行简单的重构，顺便了解下quartz.

[quartz 常识](./quartz-introduction.md)

### 入口类 LcxmQuartzAutoConfiguration
 - 注册了 `UnifyTaskEntry`：
 - 注册了`CommonJobQuartzHelper`: 工具类，添加、运行、删除、暂停定时任务
 - 扫描定时任务、定时任务日志、日志明细相关service到spring
 - 扫描上述表Mapper到mybatis

### 定时任务函数标记注解：TaskJobFlag

- 标记一个`AbstractTaskJob`子类的一个方法，标记为定时任务，注解需要一个任务的唯一标识
- 只能标记: 
  - public方法，
  - 且返回值为String, 
  - 且方法参数为`TaskJobLog`

### 定时任务的基类AbstractTaskJob

- 该类是注册定时任务处理器的基类， 处理器注册到spring
- 然后在需要需要作为定时任务的函数上标记`TaskJobFlag`

### 统一的任务入口：UnifyTaskEntry

- 使用策略模式加枚举的方式，减少JobFactory的膨胀，
- 为持有所有的`AbstractTaskJob`，在执行任务的时候，根据任务唯一标识匹配，然后执行

### 通用的Job：CommonJob

- 在通过JobBuilder 构建Job实例的时候就可以使用 CommonJob
- 持有 UnifyTaskEntry，通过其executeTask执行定时任务