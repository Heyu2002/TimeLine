package com.heyu.timeline;

import com.heyu.timeline.core.Event;
import com.heyu.timeline.core.TimeLine;
import com.heyu.timeline.core.EvictionStrategy;
import com.heyu.timeline.exception.TimeLineException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeLine淘汰策略示例转换为单元测试
 */
public class TimeLineStrategyExampleTest {
    
    @Test
    @DisplayName("测试丢弃策略")
    public void testDiscardStrategy() throws TimeLineException {
        TimeLine<Integer> timeLine = new TimeLine<>();
        timeLine.setEvictionStrategy(EvictionStrategy.getDiscardStrategy());
        
        // 添加第一个事件
        Event<Integer> event1 = new Event<>(1, 5, "Event 1");
        timeLine.addEvent(event1);
        
        // 添加第二个事件（不重叠）
        Event<Integer> event2 = new Event<>(10, 15, "Event 2");
        timeLine.addEvent(event2);
        
        // 添加一个与事件1重叠的事件（应该被丢弃）
        Event<Integer> event3 = new Event<>(3, 8, "Event 3 (应该被丢弃)");
        timeLine.addEvent(event3);
        
        // 验证事件数量（event3应该被丢弃）
        assertEquals(2, timeLine.getSortedEvents().size(), "应该有2个事件");
        
        // 验证保留的事件
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 1")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 2")));
    }
    
    @Test
    @DisplayName("测试延迟策略")
    public void testDelayStrategy() throws TimeLineException {
        TimeLine<Integer> timeLine = new TimeLine<>();
        timeLine.setEvictionStrategy(EvictionStrategy.getDelayStrategy());
        
        // 添加第一个事件
        Event<Integer> event1 = new Event<>(1, 5, "Event 1");
        timeLine.addEvent(event1);
        
        // 添加第二个事件（不重叠）
        Event<Integer> event2 = new Event<>(10, 15, "Event 2");
        timeLine.addEvent(event2);
        
        // 添加一个与事件1重叠的事件（应该被延迟）
        Event<Integer> event3 = new Event<>(3, 8, "Event 3 (应该被延迟)");
        timeLine.addEvent(event3);
        
        // 验证事件数量（event3应该被延迟而不是丢弃）
        assertEquals(3, timeLine.getSortedEvents().size(), "应该有3个事件");
        
        // 验证所有事件都被保留
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 1")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 2")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 3 (应该被延迟)")));
    }
}