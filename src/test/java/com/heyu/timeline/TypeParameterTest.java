package com.heyu.timeline;

import com.heyu.timeline.calculator.TimeCalculatorFactory;
import com.heyu.timeline.core.Event;
import com.heyu.timeline.core.OverlappingTimeLine;
import com.heyu.timeline.exception.TimeLineException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeParameterTest {

    @Test
    public void testGenericTimeCalculatorWithOverlappingTimeline() throws TimeLineException {
        // 创建一个使用Integer时间类型的OverlappingTimeLine
        OverlappingTimeLine<Integer> timeline = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Integer.class));
        
        // 添加一个有明确时间的事件
        Event<Integer> event1 = new Event<>(10, 20, "Meeting");
        timeline.addEvent(event1);
        
        // 添加一个只有持续时间的事件
        Event<Integer> event2 = new Event<>(5, "Task"); // 持续时间为5
        timeline.addEvent(event2);
        
        // 验证自动安排的事件时间
        assertEquals(Integer.valueOf(0), event2.getStart()); // 应该从零点开始
        assertEquals(Integer.valueOf(5), event2.getEnd());   // 结束时间应该是5
    }
}