package com.heyu.timeline;

import com.heyu.timeline.calculator.TimeCalculatorFactory;
import com.heyu.timeline.core.model.Event;
import com.heyu.timeline.core.timeline.OverlappingTimeLine;
import com.heyu.timeline.exception.TimeLineException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaTimeTypesTest {

    @Test
    public void testAutoScheduleWithDateTimeline() throws TimeLineException {
        // 创建一个使用Date时间类型的OverlappingTimeLine
        OverlappingTimeLine<Date> timeline = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Date.class));
        
        // 创建一些测试事件
        Date startTime = new Date(System.currentTimeMillis());
        Date endTime = new Date(startTime.getTime() + 3600000); // 1小时后
        Event<Date> event1 = new Event<>(startTime, endTime, "Meeting");
        timeline.addEvent(event1);
        
        // 添加一个只有持续时间的事件（1800000毫秒 = 30分钟）
        Date duration = new Date(1800000);
        Event<Date> event2 = new Event<>(duration, "Task");
        timeline.addEvent(event2);
        
        // 验证自动安排的事件时间
        assertEquals(new Date(0), event2.getStart()); // 应该从零点开始
        assertEquals(new Date(1800000), event2.getEnd());   // 结束时间应该是1800000毫秒
    }
    
    @Test
    public void testAutoScheduleWithDurationTimeline() throws TimeLineException {
        // 创建一个使用Duration时间类型的OverlappingTimeLine
        OverlappingTimeLine<Duration> timeline = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(Duration.class));
        
        // 创建一些测试事件
        Duration start = Duration.ofHours(2);
        Duration end = Duration.ofHours(3);
        Event<Duration> event1 = new Event<>(start, end, "Meeting");
        timeline.addEvent(event1);
        
        // 添加一个只有持续时间的事件
        Duration duration = Duration.ofMinutes(30);
        Event<Duration> event2 = new Event<>(duration, "Task");
        timeline.addEvent(event2);
        
        // 验证自动安排的事件时间
        assertEquals(Duration.ZERO, event2.getStart()); // 应该从零点开始
        assertEquals(Duration.ofMinutes(30), event2.getEnd());   // 结束时间应该是30分钟
    }
    
    @Test
    public void testAutoScheduleWithLocalDateTimeTimeline() throws TimeLineException {
        // 创建一个使用LocalDateTime时间类型的OverlappingTimeLine
        OverlappingTimeLine<LocalDateTime> timeline = new OverlappingTimeLine<>();
        
        // 设置时间计算器
        timeline.setTimeCalculator(TimeCalculatorFactory.createTimeCalculator(LocalDateTime.class));
        
        // 创建一些测试事件
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime end = start.plusHours(1);
        Event<LocalDateTime> event1 = new Event<>(start, end, "Meeting");
        timeline.addEvent(event1);
        
        // 添加一个只有持续时间的事件
        LocalDateTime duration = LocalDateTime.of(2000, 1, 1, 1, 30); // 表示1小时30分钟
        Event<LocalDateTime> event2 = new Event<>(duration, "Task");
        timeline.addEvent(event2);
        
        // 验证自动安排的事件时间
        LocalDateTime zero = LocalDateTime.of(2000, 1, 1, 0, 0);
        assertEquals(zero, event2.getStart()); // 应该从零点开始
        assertTrue(event2.getEnd().isAfter(zero));   // 结束时间应该在零点之后
    }
}