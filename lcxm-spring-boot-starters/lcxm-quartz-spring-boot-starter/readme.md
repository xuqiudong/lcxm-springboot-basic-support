## quartz  starter
> 2025-01

> 虽然对quartz不甚熟悉，但是几年前还是写了个starter 在项目中使用，也没什么太大的问题，这次准备进行简单的重构，顺便了解下quartz.

[quartz 常识](./quartz-introduction.md)

### 入口类 LcxmQuartzAutoConfiguration
 - 注册了 `TaskHandlerRegistry` 和 `UnifyTaskEntry`：
 - 注册了`CommonJobQuartzHelper`: 工具类，添加、运行、删除、暂停定时任务
 - 扫描定时任务、定时任务日志、日志明细相关service到spring
 - 扫描上述表Mapper到mybatis

### 定时任务函数标记注解：TaskJobFlag

- 标记一个`AbstractTaskJob`子类的一个方法，标记为定时任务，注解需要一个任务的唯一标识
- 只能标记: 
  - public 非静态方法，
  - 且返回值为非空的 `TaskJobExecutionResult`，
  - 且方法参数为`TaskJobLog`

- 注册中心对无效签名和重复编码记录 WARN 并跳过；重复编码保留先注册的方法。
- `logPolicy` 默认是 `ALL`；配置 `IGNORE_SKIPPED` 时，跳过结果会删除本次主日志及明细。

### 定时任务的基类AbstractTaskJob

- 该类是注册定时任务处理器的基类， 处理器注册到spring
- 然后在需要需要作为定时任务的函数上标记`TaskJobFlag`

### 统一的任务入口：UnifyTaskEntry

- 使用策略模式加枚举的方式，减少JobFactory的膨胀，
- 通过注册中心持有已校验的任务方法，在执行时按任务唯一标识匹配并调用

### 通用的Job：CommonJob

- 在通过JobBuilder 构建Job实例的时候就可以使用 CommonJob
- 持有 UnifyTaskEntry，通过其executeTask执行定时任务

### 任务结果与日志

- 框架先创建 `RUNNING` 主日志并传给任务方法。业务在同一方法中准备数据、判断跳过、执行处理，明细仍由业务调用 `TaskJobLogDetailService.logDetail` 写入。

| 返回结果 | 含义 | 默认 `ALL` | `IGNORE_SKIPPED` |
| --- | --- | --- | --- |
| `TaskJobExecutionResult.success("完成")` | 本次处理成功 | 完成主日志 | 完成主日志 |
| `TaskJobExecutionResult.failure("失败原因")` | 主动报告失败，不抛 Quartz 异常 | 完成失败日志 | 完成失败日志 |
| `TaskJobExecutionResult.skipped("当前 IP 没有变化")` | 本次无需处理 | 完成跳过日志 | 短事务删除本次主日志及明细 |

- 忽略跳过只控制数据库日志留存，仍打印业务结果和耗时，也仍有先创建再删除的写入成本。
- 业务实际异常会解包反射异常、记录失败日志，再传给 Quartz；不请求立即重试或取消后续调度。
- 日志处理异常会明确上报，不将已经确定的业务结果改成失败；日志删除失败时本次记录可能保留。
- 多线程业务需要自行管理子线程的用户上下文、等待执行和明细提交完成，再返回任务结果；框架不自动收集明细或接管线程。

### 存量任务迁移

- 将返回类型 `String` 改为 `TaskJobExecutionResult`，将原返回说明包装为 `success`；主动失败或无需处理时分别返回 `failure`、`skipped`，不能返回 `null`。
- 按需在注解中增加 `logPolicy = TaskJobLogPolicy.IGNORE_SKIPPED`，未配置时保留所有结果。
- 任务入口不再统一开启业务事务；依赖原事务的任务需在自己的业务服务中声明事务。
- 历史日志无需迁移；展示端如自行维护结果枚举，需支持新增的 `SKIPPED`。
- 单方法任务示例见 [TestOneJob](src/test/java/cn/xuqiudong/basic/quartz/job/TestOneJob.java)，完整流程与边界见[设计说明](task-log-redesign.md)。
