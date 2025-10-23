package com.heyu.timeline;

import com.heyu.timeline.core.EvictionStrategy;
import com.heyu.timeline.core.TimeLine;
import com.heyu.timeline.core.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EvictionStrategy测试
 */
public class EvictionStrategyTest {
    
    @Test
    @DisplayName("测试EvictionStrategy获取")
    public void testEvictionStrategyAcquisition() {
        // 测试是否可以正常使用EvictionStrategy
        TimeLine<Integer> timeLine = new TimeLine<>();
        
        // 测试获取丢弃策略
        EvictionStrategy<Integer> discardStrategy = EvictionStrategy.getDiscardStrategy();
        timeLine.setEvictionStrategy(discardStrategy);
        assertSame(discardStrategy, timeLine.getEvictionStrategy());
        
        // 测试获取延迟策略
        EvictionStrategy<Integer> delayStrategy = EvictionStrategy.getDelayStrategy();
        timeLine.setEvictionStrategy(delayStrategy);
        assertSame(delayStrategy, timeLine.getEvictionStrategy());
    }
    
    @Test
    @DisplayName("测试事件添加")
    public void testEventAddition() {
        TimeLine<Integer> timeLine = new TimeLine<>();
        timeLine.setEvictionStrategy(EvictionStrategy.getDelayStrategy());
        
        // 测试添加事件
        assertDoesNotThrow(() -> {
            timeLine.addEvent(new Event<>(1, 5, "测试事件1"));
            timeLine.addEvent(new Event<>(3, 8, "测试事件2"));
        });
        
        assertEquals(2, timeLine.getSortedEvents().size());
    }
}