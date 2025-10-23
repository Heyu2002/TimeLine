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
- `SimpleTimeCalculator` - 简单时间计算器实现

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

## 使用说明

1. 核心使用方式：
```java
// 使用支持重叠的TimeLine
OverlappingTimeLine<Integer> overlappingTimeLine = new OverlappingTimeLine<>();
Event<Integer> event = new Event<>(10, 20, "Meeting");
overlappingTimeLine.addEvent(event);

// 使用不支持重叠的TimeLine
TimeLine<Integer> timeLine = new TimeLine<>();
timeLine.addEvent(event);
```

2. 使用工厂创建时间线：
```java
OverlappingTimeLine<Integer> timeLine = TimeLineFactory.createOverlappingTimeLine();
```

3. 使用时间线池管理时间线：
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

4. 使用淘汰策略：
```java
TimeLine<Integer> timeLine = new TimeLine<>();
// 设置淘汰策略（默认为DISCARD）
timeLine.setEvictionStrategy(EvictionStrategy.getDelayStrategy());
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