package com.heyu.timeline;

import com.heyu.timeline.core.model.Event;
import com.heyu.timeline.core.timeline.OverlappingTimeLine;
import com.heyu.timeline.core.timeline.TimeLine;
import com.heyu.timeline.core.strategy.EvictionStrategy;
import com.heyu.timeline.exception.TimeLineException;
import com.heyu.timeline.factory.TimeLinePool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 同时演示两种时间线类型的示例转换为单元测试
 */
public class BothTimeLineTypesExampleTest {
    
    @BeforeEach
    public void setUp() {
        TimeLinePool.clear();
    }
    
    @Test
    @DisplayName("测试两种时间线类型")
    public void testBothTimeLineTypes() throws TimeLineException {
        // 设置最大时间线数量为4
        TimeLinePool.setMaxTimeLines(4);
        assertEquals(4, TimeLinePool.getMaxTimeLines());
        
        // 创建OverlappingTimeLine
        OverlappingTimeLine<Integer> overlappingTimeLine = TimeLinePool.getOverlappingTimeLine("overlapping");
        overlappingTimeLine.addEvent(new Event<>(1, 5, "Event 1"));
        overlappingTimeLine.addEvent(new Event<>(3, 8, "Event 2 (overlapping with Event 1)"));
        assertEquals(1, TimeLinePool.getCurrentTimeLineCount());
        
        // 创建TimeLine
        TimeLine<Integer> timeLine = TimeLinePool.getTimeLine("non-overlapping");
        timeLine.addEvent(new Event<>(10, 15, "Event A"));
        timeLine.addEvent(new Event<>(20, 25, "Event B"));
        assertEquals(2, TimeLinePool.getCurrentTimeLineCount());
        
        // 测试TimeLine中的重叠事件处理
        timeLine.setEvictionStrategy(EvictionStrategy.getDelayStrategy());
        timeLine.addEvent(new Event<>(12, 18, "Event C (overlapping with Event A, should be delayed)"));
        
        // 验证OverlappingTimeLine中的所有事件（应该都保留）
        assertEquals(2, overlappingTimeLine.getSortedEvents().size());
        assertTrue(overlappingTimeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 1")));
        assertTrue(overlappingTimeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 2 (overlapping with Event 1)")));
        
        // 验证TimeLine中的所有事件
        assertEquals(3, timeLine.getSortedEvents().size());
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event A")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event B")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event C (overlapping with Event A, should be delayed)")));
    }
    
    @Test
    @DisplayName("测试时间线池限制")
    public void testTimeLinePoolLimit() throws TimeLineException {
        // 设置最大时间线数量为2
        TimeLinePool.setMaxTimeLines(2);
        
        // 创建两个时间线
        TimeLinePool.getOverlappingTimeLine("overlapping");
        TimeLinePool.getTimeLine("non-overlapping");
        
        // 尝试创建更多的时间线以达到限制
        assertThrows(TimeLineException.class, () -> {
            TimeLinePool.getOverlappingTimeLine("overlapping2");
        });
        
        assertThrows(TimeLineException.class, () -> {
            TimeLinePool.getTimeLine("non-overlapping2");
        });
    }
}