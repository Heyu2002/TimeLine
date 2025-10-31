package com.heyu.timeline;

import com.heyu.timeline.core.model.Event;
import com.heyu.timeline.core.timeline.TimeLine;
import com.heyu.timeline.core.strategy.EvictionStrategy;
import com.heyu.timeline.exception.TimeLineException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 完整的TimeLine使用示例转换为单元测试
 */
public class CompleteTimeLineExampleTest {
    
    @Test
    @DisplayName("测试延迟策略行为")
    public void testDelayStrategyBehavior() throws TimeLineException {
        // 创建一个TimeLine实例
        TimeLine<Integer> timeLine = new TimeLine<>();
        
        // 设置淘汰策略为延迟策略
        timeLine.setEvictionStrategy(EvictionStrategy.getDelayStrategy());
        
        // 添加第一个事件
        Event<Integer> event1 = new Event<>(1, 5, "Event 1");
        timeLine.addEvent(event1);
        
        // 添加第二个不重叠的事件
        Event<Integer> event2 = new Event<>(10, 15, "Event 2");
        timeLine.addEvent(event2);
        
        // 添加一个与event1重叠的事件，应该会被延迟到event2之后
        Event<Integer> event3 = new Event<>(3, 8, "Event 3 (overlapping)");
        timeLine.addEvent(event3);
        
        // 验证所有三个事件都存在
        assertEquals(3, timeLine.getSortedEvents().size());
        
        // 验证事件都被正确添加
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 1")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 2")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 3 (overlapping)")));
    }
    
    @Test
    @DisplayName("测试丢弃策略行为")
    public void testDiscardStrategyBehavior() throws TimeLineException {
        TimeLine<Integer> timeLine2 = new TimeLine<>();
        timeLine2.setEvictionStrategy(EvictionStrategy.getDiscardStrategy());
        
        Event<Integer> event4 = new Event<>(20, 25, "Event 4");
        timeLine2.addEvent(event4);
        
        // 添加一个与event4重叠的事件，应该会被丢弃
        Event<Integer> event5 = new Event<>(22, 27, "Event 5 (overlapping, should be discarded)");
        timeLine2.addEvent(event5);
        
        // 验证只有一个事件存在（event5被丢弃）
        assertEquals(1, timeLine2.getSortedEvents().size());
        assertEquals("Event 4", timeLine2.getSortedEvents().get(0).getSubject());
    }
}