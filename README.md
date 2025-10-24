# Timeline Project

### Instructions for Use

To use this library in your Maven project:

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

For Gradle projects:

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

## Package Structure Description

### com.heyu.timeline.core
Core timeline functionality package, containing main classes and interfaces:
- `Event` - Timeline event class
- `OverlappingTimeLine` - Timeline implementation supporting overlapping events
- `TimeLine` - Timeline implementation that does not allow event overlap
- `EvictionStrategy` - Eviction strategy interface

### com.heyu.timeline.exception
Exception handling package, containing custom exception classes:
- `TimeLineException` - Custom exception for timeline-related operations

### com.heyu.timeline.calculator
Time calculation package, containing time calculation interfaces and implementations:
- `TimeCalculator` - Time calculation interface
- `SimpleTimeCalculator` - Simple time calculator implementation

### com.heyu.timeline.factory
Factory pattern package, containing factory classes and timeline pool:
- `TimeLineFactory` - Timeline factory class
- `TimeLinePool` - Timeline pool for managing timeline instances

### com.heyu.timeline.example
Example package, containing usage examples:
- `TimeLineUsageExample` - OverlappingTimeLine usage example
- `TimeLinePoolExample` - TimeLinePool usage example
- `TimeLineExample` - TimeLine usage example
- `CompleteTimeLineExample` - Complete TimeLine usage example
- `TimeLineStrategyExample` - TimeLine eviction strategy example
- `BothTimeLineTypesExample` - Comparison example of both timeline types

## Usage Instructions

### 1. Core usage:
```java
// Using TimeLine that supports overlaps
OverlappingTimeLine<Integer> overlappingTimeLine = new OverlappingTimeLine<>();
Event<Integer> event = new Event<>(10, 20, "Meeting");
overlappingTimeLine.addEvent(event);

// Using TimeLine that does not support overlaps
TimeLine<Integer> timeLine = new TimeLine<>();
timeLine.addEvent(event);
```

### 2. Creating timelines using factory:
```java
OverlappingTimeLine<Integer> timeLine = TimeLineFactory.createOverlappingTimeLine();
```

### 3. Managing timelines using timeline pool:
```java
// Set maximum number of timelines (default is 3)
TimeLinePool.setMaxTimeLines(5);

// Get OverlappingTimeLine
OverlappingTimeLine<Integer> overlappingTimeLine = TimeLinePool.getOverlappingTimeLine("myOverlappingTimeline");

// Get TimeLine
TimeLine<Integer> timeLine = TimeLinePool.getTimeLine("myTimeline");

// Get current timeline count
int count = TimeLinePool.getCurrentTimeLineCount();
```

### 4. Using eviction strategies:
```java
TimeLine<Integer> timeLine = new TimeLine<>();
// Set eviction strategy (default is DISCARD)
timeLine.setEvictionStrategy(EvictionStrategy.getDelayStrategy());
```



## Feature Description

### TimeLine vs OverlappingTimeLine
- `OverlappingTimeLine`: Allows events to overlap in time
- `TimeLine`: Does not allow events to overlap in time, handles conflicts through eviction strategies

### Eviction Strategies
- `EvictionStrategy.getDiscardStrategy()`: When a new event conflicts with existing events, discard the new event directly
- `EvictionStrategy.getDelayStrategy()`: When a new event conflicts with existing events, delay the new event to the end of the timeline

### Timeline Pool
- Controls the number of created timelines to prevent resource exhaustion
- Default maximum quantity is 3, can be modified via `setMaxTimeLines` method
- Supports two types of timelines: `OverlappingTimeLine` and `TimeLine`
- Get different types of timelines through different methods:
  - `getOverlappingTimeLine()` - Get OverlappingTimeLine instance
  - `getTimeLine()` - Get TimeLine instance