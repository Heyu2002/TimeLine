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
Time calculation package, containing