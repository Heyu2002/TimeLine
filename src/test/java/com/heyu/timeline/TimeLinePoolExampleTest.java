package com.heyu.timeline;

import com.heyu.timeline.core.Event;
import com.heyu.timeline.core.OverlappingTimeLine;
import com.heyu.timeline.exception.TimeLineException;
import com.heyu.timeline.factory.TimeLinePool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeLinePool使用示例转换为单元测试
 */
public class TimeLinePoolExampleTest {
    
    @BeforeEach
    public void setUp() {
        // 每次测试前清空时间线池
        TimeLinePool.clear();
    }
    
    @Test
    @DisplayName("测试TimeLinePool基本功能")
    public void testTimeLinePoolBasicFunctionality() throws TimeLineException {
        // 设置最大时间线数量为2
        TimeLinePool.setMaxTimeLines(2);
        assertEquals(2, TimeLinePool.getMaxTimeLines());
        
        // 创建第一个时间线
        OverlappingTimeLine<Integer> timeLine1 = TimeLinePool.getOverlappingTimeLine("timeline1");
        timeLine1.addEvent(new Event<>(1, 5, "Event 1"));
        assertEquals(1, TimeLinePool.getCurrentTimeLineCount());
        
        // 创建第二个时间线
        OverlappingTimeLine<Integer> timeLine2 = TimeLinePool.getOverlappingTimeLine("timeline2");
        timeLine2.addEvent(new Event<>(10, 15, "Event 2"));
        assertEquals(2, TimeLinePool.getCurrentTimeLineCount());
        
        // 尝试创建第三个时间线，应该会抛出异常
        assertThrows(TimeLineException.class, () -> {
            TimeLinePool.getOverlappingTimeLine("timeline3");
        });
        
        // 获取已存在的时间线
        OverlappingTimeLine<Integer> existingTimeLine = TimeLinePool.getOverlappingTimeLine("timeline1");
        assertSame(timeLine1, existingTimeLine);
        
        // 移除一个时间线
        TimeLinePool.removeTimeLine("timeline2");
        assertEquals(1, TimeLinePool.getCurrentTimeLineCount());
        
        // 现在可以创建新的时间线了
        OverlappingTimeLine<Integer> timeLine3 = TimeLinePool.getOverlappingTimeLine("timeline3");
        assertNotNull(timeLine3);
        assertEquals(2, TimeLinePool.getCurrentTimeLineCount());
    }
}