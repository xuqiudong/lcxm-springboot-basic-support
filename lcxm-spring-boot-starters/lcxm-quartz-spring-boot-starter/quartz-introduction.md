## quartz  starter
> 2025-01

> 虽然对quartz不甚熟悉，但是几年前还是写了个starter 在项目中使用，也没什么太大的问题，这次准备进行简单的重构，顺便了解下quartz.

### 

### 一 quartz简介

#### 1 、基本组件

##### Job

- `Job`是一个任务接口，开发者定义自己的任务须实现该接口，并重写`execute(JobExecutionContext context)`方法.
- `Job`中的任务有可能并发执行，例如任务的执行时间过长，而每次触发的时间间隔太短，则会导致任务会被并发执行。
- 为了避免出现上面的问题，可以在`Job`实现类上使用`@DisallowConcurrentExecution`,保证上一个任务执行完后，再去执行下一个任务

##### JobDetail

- `JobDetail`是任务详情。
- 包含有：任务名称，任务组名称，任务描述、具体任务Job的实现类、参数配置等等信息
- 可以说`JobDetail`是任务的定义，而`Job`是任务的执行逻辑。

##### Trigger

- Trigger是一个触发器,定义Job执行的时间规则。
- 主要触发器:`SimpleTrigger`,`CronTrigger`,`CalendarIntervalTrigger`,`DailyTimeIntervalTrigger`。
- `SimpleTrigger`:从某一个时间开始，以一定的时间间隔来执行任务,重复多少次。
- `CronTrigger`: 适合于复杂的任务，使用cron表达式来定义执行规则。
- `CalendarIntervalTrigger`:指定从某一个时间开始，以一定的时间间隔执行的任务,时间间隔比`SimpleTrigger`丰富
- `DailyTimeIntervalTrigger`:指定每天的某个时间段内，以一定的时间间隔执行任务。并且它可以支持指定星期。
- 所有的Trigger都包含了`StartTime`和`endTime`这两个属性，用来指定`Trigger`被触发的时间区间。
- 所有的Trigger都可以设置`MisFire`策略.
- `MisFire`策略是对于由于系统奔溃或者任务时间过长等原因导致Trigger在应该触发的时间点没有触发.
- 并且超过了`misfireThreshold`设置的时间（默认是一分钟，没有超过就立即执行）就算misfire(失火)了。
- 这个时候就该设置如何应对这种变化了。激活失败指令（`Misfire Instructions`）是触发器的一个重要属性

**发生Misfire 对于SimpleTrigger的处理策略**

```
//将任务马上执行一次。对于不会重复执行的任务，这是默认的处理策略。
MISFIRE_INSTRUCTION_FIRE_NOW = 1;

// 调度引擎重新调度该任务，立即执行任务，repeat count 保持不变，
// 按照原有制定的执行方案执行repeat count次，但是，如果当前时间，已经晚于end-time，那么这个触发器将不会再被触发
// 简单的说就是，错过了应该触发的时间没有按时执行，但是最终它还是以原来的重复次数执行，就是会比预计终止的时间晚。
MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT

//和上面的类似，区别就是不会立马执行，而是在下一个激活点执行，且超时期内错过的执行机会作废。        
MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT

// 这个也是重新调度任务，但是它只按照剩余次数来触发，
// 比如，应该执行10次，但是中间错过了3次没有执行，那它最终只会执行剩余次数 7次。
MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT

//在下一个激活点执行，并重复到指定的次数。
MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT

// 立即执行任务，repeat count 保持不变，就算到了endtime,也继续执行
MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
```

**发生Misfire 对于其他的Trigger**

```
//立刻执行一次，然后就按照正常的计划执行。
MISFIRE_INSTRUCTION_FIRE_ONCE_NOW

//目前不执行，然后就按照正常的计划执行。这意味着如果下次执行时间超过了end time，实际上就没有执行机会了。
MISFIRE_INSTRUCTION_DO_NOTHING
```

##### **Scheduler**

- 调度器，主要是用来管理`Trigger`、`JobDetail`的。
- `Scheduler`可以通过组名或者名称来对`Trigger`和`JobDetail`来进行管理
- 一个`Trigger`只能对应一个`Job`，但是一个`Job`可以对应多个`Trigger`.
- `Scheduler` 有两个实现类：`RemoteScheduler`、`StdScheduler`。但它是由`SchdulerFactory`创建的。
- `SchdulerFactory`是个接口，它有两个实现类：`StdSchedulerFactory`、`DirectSchedulerFactory`

#### 2、**相关Builder介绍**

##### **JobBuilder**

这个主要方便我们构建任务详情，常用方法：

- `withIdentity(String name, String group)`:配置Job名称与组名
- `withDescription(String jobDescription)`: 任务描述
- `requestRecovery()`: 出现故障是否重新执行，默认false
- `storeDurably()`: 作业完成后是否保留存储，默认false
- `usingJobData(String dataKey, String value)`: 配置单个参数key
- `usingJobData(JobDataMap newJobDataMap)`: 配置多个参数，放入一个map
- `setJobData(JobDataMap newJobDataMap)`: 和上面类似，但是这个参数直接指向newJobDataMap,直接设置的参数无效

##### **TriggerBuilder**

这个主要方便我们构建触发器，常用方法：

- `withIdentity(String name, String group)`： 配置Trigger名称与组名
- `withIdentity(TriggerKey triggerKey)`： 配置Trigger名称与组名
- `withDescription(String triggerDescription)`： 描述
- `withPriority(int triggerPriority)`： 设置优先级，默认是：5
- `startAt(Date triggerStartTime)`： 设置开始时间
- `startNow()`： 触发器立即生效
- `endAt(Date triggerEndTime)`： 设置结束时间
- `withSchedule(ScheduleBuilder schedBuilder)`： 设置调度builder,下面的builder就是

##### **SimpleScheduleBuilder**

几种触发器类型之一，最简单常用的。常用方法：

- `repeatForever()`：指定触发器将无限期重复
- `withRepeatCount(int triggerRepeatCount)`：指定重复次数，总触发的次数=triggerRepeatCount+1
- `repeatSecondlyForever(int seconds)`：每隔seconds秒无限期重复
- `repeatMinutelyForever(int minutes)`：每隔minutes分钟无限期重复
- `repeatHourlyForever(int hours)`：每隔hours小时无限期重复
- `repeatSecondlyForever()`：每隔1秒无限期重复
- `repeatMinutelyForever()`：每隔1分钟无限期重复
- `repeatHourlyForever()`：每隔1小时无限期重复
- `withIntervalInSeconds(int intervalInSeconds)`：每隔intervalInSeconds秒执行
- `withIntervalInMinutes(int intervalInMinutes)`：每隔intervalInMinutes分钟执行
- `withIntervalInHours(int intervalInHours)`：每隔intervalInHours小时执行
- `withMisfireHandlingInstructionFireNow()`：失火后的策略为：`MISFIRE_INSTRUCTION_FIRE_NOW`

##### **CronScheduleBuilder**

算是非常常用的了，crontab 表达式，常用方法：

- `cronSchedule(String cronExpression)`：使用cron表达式

##### **CalendarIntervalScheduleBuilder**

常用方法：

- `inTimeZone(TimeZone timezone)`：设置时区
- `withInterval(int timeInterval, IntervalUnit unit)`：相隔多少时间执行，单位有：毫秒、秒、分、时、天、周、月、年
- `withIntervalInSeconds(int intervalInSeconds)`：相隔秒
- `withIntervalInWeeks(int intervalInWeeks)`：相隔周
- `withIntervalInMonths(int intervalInMonths)`：相隔月

等等方法

##### **DailyTimeIntervalScheduleBuilder**

- `withInterval(int timeInterval, IntervalUnit unit)`：相隔多少时间执行，单位有：秒、分、时，其他单位的不支持会报错
- `withIntervalInSeconds(int intervalInSeconds)`：相隔秒
- `withIntervalInMinutes(int intervalInMinutes)`：相隔分
- `withIntervalInHours(int intervalInHours)`：相隔时
- `onDaysOfTheWeek(Set onDaysOfWeek)`：将触发器设置为在一周的指定日期触发。取值范围可以是1-7，1是星期天，2是星期一…
- `onDaysOfTheWeek(Integer ... onDaysOfWeek)`：和上面一样，3是星期二…7是星期六
- `onMondayThroughFriday()`：每星期的周一导周五触发
- `onSaturdayAndSunday()`：每星期的周六周日触发
- `onEveryDay()`：每天触发
- `withRepeatCount(int repeatCount)`：重复次数，总的重复次数=`1 (at start time) + repeatCount`
- `startingDailyAt(TimeOfDay timeOfDay)`：触发的开始时间
- `endingDailyAt(TimeOfDay timeOfDay)`：触发的结束时间

#### 3、表说明

github下载地址：**https://github.com/quartz-scheduler/quartz/blob/quartz-2.3.x/quartz-core/src/main/resources/org/quartz/impl/jdbcjobstor**

```
//以Blob 类型存储的触发器。 
qrtz_blob_triggers

//存放日历信息， quartz可配置一个日历来指定一个时间范围。 
qrtz_calendars

//存放cron类型的触发器。 
qrtz_cron_triggers
//存储已经触发的trigger相关信息，trigger随着时间的推移状态发生变化，直到最后trigger执行完成，从表中被删除。 
qrtz_fired_triggers 

//存放一个jobDetail信息。 
qrtz_job_details

//job**监听器**。 
qrtz_job_listeners

//Quartz提供的锁表，为多个节点调度提供分布式锁，实现分布式调度，默认有2个锁
qrtz_locks

//存放暂停掉的触发器。
qrtz_paused_trigger_graps

//存储所有节点的scheduler，会定期检查scheduler是否失效
qrtz_scheduler_state

//存储SimpleTrigger 
qrtz_simple_triggers

//触发器监听器。 
qrtz_trigger_listeners

//触发器的基本信息。
qrtz_triggers

//存储CalendarIntervalTrigger和DailyTimeIntervalTrigger两种类型的触发器
qrtz_simprop_triggers
```

#### 4、springboot自动装备入口`QuartzAutoConfiguration`

1. 自动注册了 `SchedulerFactoryBean`
2. 自动注册了`Scheduler`
   - 因为`SchedulerFactoryBean`是一个`FactoryBean`，其getObject方法返回的是`Scheduler`， 所以也自动注册了`Scheduler`到spring容器

参考：

- [SpringBoot 集成 Quartz](https://cloud.tencent.com/developer/article/2222436)