# Timeline Project

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
- `TimeCalculatorFactory` - Factory class for creating time calculators
- `GenericTimeCalculator` - Generic time calculator implementation

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

## Building and Testing

### Prerequisites
- Java 8 or higher
- Maven 3.6 or higher

### Building the Project
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

### Running All Tests (including integration tests)
```bash
mvn verify
```

### Packaging the Project
```bash
mvn package
```

This will create a JAR file in the `target/` directory.

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

### 5. Automatic time slot assignment:
```java
// Create a timeline with a time calculator
OverlappingTimeLine<Integer> timeline = new OverlappingTimeLine<>();
timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Integer.class));

// Add events with specific time ranges
Event<Integer> event1 = new Event<>(10, 20, "Meeting");
timeline.addEvent(event1);

// Add events with only duration - they will be automatically scheduled
Event<Integer> event2 = new Event<>(5, "Quick Task"); // Duration of 5 time units
timeline.addEvent(event2); // Will be automatically scheduled from 0 to 5

Event<Integer> event3 = new Event<>(3, "Another Task"); // Duration of 3 time units
timeline.addEvent(event3); // Will be automatically scheduled from 5 to 8
```

### 6. Working with Java time types:
```java
// Using Date type
OverlappingTimeLine<Date> dateTimeline = new OverlappingTimeLine<>();
dateTimeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Date.class));

// Using Duration type
OverlappingTimeLine<Duration> durationTimeline = new OverlappingTimeLine<>();
durationTimeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Duration.class));

// Using LocalDateTime type
OverlappingTimeLine<LocalDateTime> localDateTimeTimeline = new OverlappingTimeLine<>();
localDateTimeTimeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(LocalDateTime.class));
```

### 7. Adding dependency via JitPack

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

### Automatic Time Slot Assignment
- Events with only duration information can be automatically scheduled
- The system finds suitable time slots based on existing events
- Requires a `TimeCalculator` to be set on the timeline
- Time slots are assigned in a non-overlapping manner when possible
- Supports various time types including numeric types and Java time types (Date, LocalDateTime, Duration, etc.)
- Uses factory pattern to create appropriate time calculators for different time types