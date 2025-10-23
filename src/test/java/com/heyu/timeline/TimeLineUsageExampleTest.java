package com.heyu.timeline;

import com.heyu.timeline.calculator.SimpleTimeCalculator;
import com.heyu.timeline.core.Event;
import com.heyu.timeline.core.OverlappingTimeLine;
import com.heyu.timeline.exception.TimeLineException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 时间线使用示例转换为单元测试
 */
public class TimeLineUsageExampleTest {
    
    @Test
    @DisplayName("测试OverlappingTimeLine基本功能")
    public void testOverlappingTimeLineBasicFunctionality() throws TimeLineException {
        // 创建一个基于整数时间线的时间线
        OverlappingTimeLine<Integer> timeLine = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeLine.setTimeCalculator(new SimpleTimeCalculator<>());
        
        // 创建有明确时间的事件
        Event<Integer> event1 = new Event<>(10, 20, "Meeting");
        timeLine.addEvent(event1);
        
        // 创建只有持续时间的事件
        Event<Integer> event2 = new Event<>(5, "Quick Task");
        // 注意：自动安排时间段的功能需要根据具体需求实现
        // 这里只是展示如何使用这个功能
        
        // 获取所有活跃事件
        List<Event<Integer>> activeEvents = timeLine.getSortedEvents();
        assertEquals(1, activeEvents.size(), "应该有1个活跃事件");
        
        // 激活/停用事件
        event1.deactivate();
        assertEquals(0, timeLine.getSortedEvents().size(), "停用后应该有0个活跃事件");
        
        event1.activate();
        assertEquals(1, timeLine.getSortedEvents().size(), "重新激活后应该有1个活跃事件");
        
        // 移除事件
        timeLine.removeEvent(event1);
        assertEquals(0, timeLine.getSortedEvents().size(), "移除后应该有0个活跃事件");
    }
}