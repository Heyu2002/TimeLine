# Timeline Project

## 包结构说明

### com.heyu.timeline.core
核心时间线功能包，包含主要的类和接口：
- `Event` - 时间线事件类
- `OverlappingTimeLine` - 支持重叠事件的时间线实现
- `TimeLine` - 不允许事件重叠的时间线实现
- `EvictionStrategy` - 淘汰策略接口

### com.heyu.timeline.exception
异常处理包，包含自定义异常类：
- `TimeLineException` - 时间线相关操作的自定义异常

### com.heyu.timeline.calculator
时间计算包，包含时间计算接口和实现：
- `TimeCalculator` - 时间计算接口
- `TimeCalculatorFactory` - 时间计算器工厂类
- `GenericTimeCalculator` - 通用时间计算器实现

### com.heyu.timeline.factory
工厂模式包，包含工厂类和时间线池：
- `TimeLineFactory` - 时间线工厂类
- `TimeLinePool` - 时间线池，用于管理时间线实例
- `TimeLines` - 时间线实体类

### com.heyu.timeline.example
示例包，包含使用示例：
- `TimeLineUsageExample` - OverlappingTimeLine使用示例
- `TimeLinePoolExample` - TimeLinePool使用示例
- `TimeLineExample` - TimeLine使用示例
- `CompleteTimeLineExample` - 完整的TimeLine使用示例
- `TimeLineStrategyExample` - TimeLine淘汰策略示例
- `BothTimeLineTypesExample` - 两种时间线类型对比示例

## 构建和测试

### 前提条件
- Java 8 或更高版本
- Maven 3.6 或更高版本

### 构建项目
```bash
mvn clean compile
```

### 运行测试
```bash
mvn test
```

### 运行所有测试（包括集成测试）
```bash
mvn verify
```

### 打包项目
```bash
mvn package
```

这将在 `target/` 目录中创建一个 JAR 文件。

## 使用说明

### 1. 核心使用方式：
```java
// 使用支持重叠的TimeLine
OverlappingTimeLine<Integer> overlappingTimeLine = new OverlappingTimeLine<>();
Event<Integer> event = new Event<>(10, 20, "Meeting");
overlappingTimeLine.addEvent(event);

// 使用不支持重叠的TimeLine
TimeLine<Integer> timeLine = new TimeLine<>();
timeLine.addEvent(event);
```

### 2. 使用工厂创建时间线：
```java
OverlappingTimeLine<Integer> timeLine = TimeLineFactory.createOverlappingTimeLine();
```

### 3. 使用时间线池管理时间线：
```java
// 设置最大时间线数量（默认为3）
TimeLinePool.setMaxTimeLines(5);

// 获取OverlappingTimeLine
OverlappingTimeLine<Integer> overlappingTimeLine = TimeLinePool.getOverlappingTimeLine("myOverlappingTimeline");

// 获取TimeLine
TimeLine<Integer> timeLine = TimeLinePool.getTimeLine("myTimeline");

// 获取当前时间线数量
int count = TimeLinePool.getCurrentTimeLineCount();
```

### 4. 使用淘汰策略：
```java
TimeLine<Integer> timeLine = new TimeLine<>();
// 设置淘汰策略（默认为DISCARD）
timeLine.setEvictionStrategy(EvictionStrategy.getDelayStrategy());
```

### 5. 自动时间段分配：
```java
// 创建带时间计算器的时间线
OverlappingTimeLine<Integer> timeline = new OverlappingTimeLine<>();
timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Integer.class));

// 添加有明确时间段的事件
Event<Integer> event1 = new Event<>(10, 20, "会议");
timeline.addEvent(event1);

// 添加只有持续时间的事件 - 它们将被自动安排时间段
Event<Integer> event2 = new Event<>(5, "快速任务"); // 持续时间为5个时间单位
timeline.addEvent(event2); // 将被自动安排在0到5时间段

Event<Integer> event3 = new Event<>(3, "另一个任务"); // 持续时间为3个时间单位
timeline.addEvent(event3); // 将被自动安排在5到8时间段
```

### 6. 使用Java时间类型：
```java
// 使用Date类型
OverlappingTimeLine<Date> dateTimeline = new OverlappingTimeLine<>();
dateTimeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Date.class));

// 使用Duration类型
OverlappingTimeLine<Duration> durationTimeline = new OverlappingTimeLine<>();
durationTimeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Duration.class));

// 使用LocalDateTime类型
OverlappingTimeLine<LocalDateTime> localDateTimeTimeline = new OverlappingTimeLine<>();
localDateTimeTimeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(LocalDateTime.class));
```

### 7. 通过 JitPack 添加依赖

在 Maven 项目中使用此库：

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Heyu2002</groupId>
    <artifactId>TimeLine</artifactId>
    <version>v0.0.0</version>
</dependency>
```

对于 Gradle 项目：

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.Heyu2002:TimeLine:v0.0.0'
}
```

## 特性说明

### TimeLine vs OverlappingTimeLine
- `OverlappingTimeLine`: 允许事件在时间上重叠
- `TimeLine`: 不允许事件在时间上重叠，通过淘汰策略处理冲突

### 淘汰策略
- `EvictionStrategy.getDiscardStrategy()`: 当新事件与现有事件冲突时，直接丢弃新事件
- `EvictionStrategy.getDelayStrategy()`: 当新事件与现有事件冲突时，将新事件延迟到时间线末尾

### 时间线池
- 控制创建的时间线数量，防止资源耗尽
- 默认最大数量为3，可通过`setMaxTimeLines`方法修改
- 支持两种类型的时间线：`OverlappingTimeLine`和`TimeLine`
- 通过不同方法获取不同类型的时间线：
  - `getOverlappingTimeLine()` - 获取OverlappingTimeLine实例
  - `getTimeLine()` - 获取TimeLine实例

### 自动时间段分配
- 只有持续时间信息的事件可以被自动安排时间段
- 系统会根据现有事件自动寻找合适的时间段
- 需要在时间线上设置`TimeCalculator`
- 时间段会尽可能以非重叠方式分配
- 支持多种时间类型，包括数值类型和Java时间类型（Date、LocalDateTime、Duration等）
- 使用工厂模式为不同时间类型创建适当的时间计算器