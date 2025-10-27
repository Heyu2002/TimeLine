package com.heyu.timeline;

import com.heyu.timeline.calculator.TimeCalculatorFactory;
import com.heyu.timeline.core.Event;
import com.heyu.timeline.core.OverlappingTimeLine;
import com.heyu.timeline.exception.TimeLineException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AutoScheduleTest {

    @Test
    public void testAutoScheduleWithIntegerTimeline() throws TimeLineException {
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
    
    @Test
    public void testAutoScheduleBetweenEvents() throws TimeLineException {
        // 创建一个使用Integer时间类型的OverlappingTimeLine
        OverlappingTimeLine<Integer> timeline = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Integer.class));
        
        // 添加两个有明确时间的事件，中间有空隙
        Event<Integer> event1 = new Event<>(5, 10, "Morning Meeting");
        Event<Integer> event2 = new Event<>(20, 30, "Afternoon Meeting");
        timeline.addEvent(event1);
        timeline.addEvent(event2);
        
        // 添加一个只有持续时间的事件，持续时间为8
        Event<Integer> event3 = new Event<>(8, "Task");
        timeline.addEvent(event3);
        
        // 验证事件被安排在间隙中（因为中间空隙足够）
        assertEquals(Integer.valueOf(10), event3.getStart()); // 应该在第一个事件之后开始
        assertEquals(Integer.valueOf(18), event3.getEnd());   // 结束时间应该是18
    }
    
    @Test
    public void testAutoScheduleInGap() throws TimeLineException {
        // 创建一个使用Integer时间类型的OverlappingTimeLine
        OverlappingTimeLine<Integer> timeline = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Integer.class));
        
        // 添加两个有明确时间的事件，中间有足够大的空隙
        Event<Integer> event1 = new Event<>(5, 10, "Morning Meeting");
        Event<Integer> event2 = new Event<>(20, 30, "Afternoon Meeting");
        timeline.addEvent(event1);
        timeline.addEvent(event2);
        
        // 添加一个只有持续时间的事件，持续时间为5
        Event<Integer> event3 = new Event<>(5, "Task");
        timeline.addEvent(event3);
        
        // 验证事件被安排在间隙中（因为中间空隙足够）
        assertEquals(Integer.valueOf(10), event3.getStart()); // 应该在第一个事件之后开始
        assertEquals(Integer.valueOf(15), event3.getEnd());   // 结束时间应该是15
    }
    
    @Test
    public void testAutoScheduleWithLongTimeline() throws TimeLineException {
        // 创建一个使用Long时间类型的OverlappingTimeLine
        OverlappingTimeLine<Long> timeline = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Long.class));
        
        // 添加一个有明确时间的事件
        Event<Long> event1 = new Event<>(10L, 20L, "Meeting");
        timeline.addEvent(event1);
        
        // 添加一个只有持续时间的事件
        Event<Long> event2 = new Event<>(5L, "Task"); // 持续时间为5
        timeline.addEvent(event2);
        
        // 验证自动安排的事件时间
        assertEquals(Long.valueOf(0), event2.getStart()); // 应该从零点开始
        assertEquals(Long.valueOf(5), event2.getEnd());   // 结束时间应该是5
    }
}