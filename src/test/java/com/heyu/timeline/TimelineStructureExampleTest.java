package com.heyu.timeline;

import com.heyu.timeline.core.model.Event;
import com.heyu.timeline.core.timeline.TimelineStructure;
import com.heyu.timeline.factory.TimeLineFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TimelineStructure接口使用示例转换为单元测试
 */
public class TimelineStructureExampleTest {
    
    @Test
    @DisplayName("测试TimelineStructure多态性")
    public void testTimelineStructurePolymorphism() throws Exception {
        // 使用接口引用创建OverlappingTimeLine实例
        TimelineStructure<Integer> overlappingTimeline = TimeLineFactory.createOverlappingTimeLine();
        overlappingTimeline.addEvent(new Event<>(1, 5, "Event 1"));
        overlappingTimeline.addEvent(new Event<>(3, 8, "Event 2 (overlaps with Event 1)"));
        
        // 验证OverlappingTimeLine事件
        assertEquals(2, overlappingTimeline.getSortedEvents().size());
        assertTrue(overlappingTimeline.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 1")));
        assertTrue(overlappingTimeline.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event 2 (overlaps with Event 1)")));
        
        // 使用接口引用创建TimeLine实例
        TimelineStructure<Integer> timeline = TimeLineFactory.createTimeLine();
        timeline.addEvent(new Event<>(10, 15, "Event A"));
        timeline.addEvent(new Event<>(20, 25, "Event B"));
        
        // 验证TimeLine事件
        assertEquals(2, timeline.getSortedEvents().size());
        assertTrue(timeline.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event A")));
        assertTrue(timeline.getSortedEvents().stream()
                .anyMatch(e -> e.getSubject().equals("Event B")));
        
        // 演示多态性 - 使用接口类型处理不同的实现
        demonstratePolymorphism(overlappingTimeline);
        demonstratePolymorphism(timeline);
    }
    
    /**
     * 演示多态性 - 使用接口类型处理不同的实现
     * @param timeline 时间线实例
     */
    private void demonstratePolymorphism(TimelineStructure<Integer> timeline) {
        assertTrue(timeline.getAllEvents().size() > 0);
        assertTrue(timeline.getSortedEvents().size() >= 0);
    }
}