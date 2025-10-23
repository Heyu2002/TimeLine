package com.heyu.timeline;

import com.heyu.timeline.core.Event;
import com.heyu.timeline.core.TimeLine;
import com.heyu.timeline.exception.TimeLineException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeLine使用示例转换为单元测试
 */
public class TimeLineExampleTest {
    
    @Test
    @DisplayName("测试TimeLine基本功能")
    public void testTimeLineBasicFunctionality() throws TimeLineException {
        // 创建一个TimeLine实例
        TimeLine<Integer> timeLine = new TimeLine<>();
        
        // 添加第一个事件
        Event<Integer> event1 = new Event<>(1, 5, "Event 1");
        timeLine.addEvent(event1);
        
        // 添加第二个不重叠的事件
        Event<Integer> event2 = new Event<>(10, 15, "Event 2");
        timeLine.addEvent(event2);
        
        // 添加一个与event1重叠的事件
        Event<Integer> event3 = new Event<>(3, 8, "Event 3");
        timeLine.addEvent(event3);
        
        // 验证事件数量（由于默认策略是丢弃，event3应该被丢弃）
        assertEquals(2, timeLine.getSortedEvents().size());
        
        // 验证事件内容
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 1")));
        assertTrue(timeLine.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 2")));
    }
}