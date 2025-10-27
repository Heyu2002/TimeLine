package com.heyu.timeline.core;

import com.heyu.timeline.exception.TimeLineException;

import java.util.List;

/**
 * 淘汰策略接口
 * @param <T> 时间类型
 */
public interface EvictionStrategy<T> {
    
    /**
     * 丢弃策略：当有冲突时直接丢弃新事件
     */
    EvictionStrategy<?> DISCARD = new DiscardEvictionStrategy();
    
    /**
     * 延迟策略：当有冲突时将新事件安排到时间线的末尾
     */
    EvictionStrategy<?> DELAY = new DelayEvictionStrategy();
    
    /**
     * 解决事件冲突
     * @param newEvent 新事件
     * @param existingEvents 现有事件列表
     * @return 解决冲突后的事件，如果返回null表示丢弃该事件
     * @throws TimeLineException 时间线异常
     */
    Event<T> resolveConflict(Event<T> newEvent, List<Event<T>> existingEvents) throws TimeLineException;
    
    /**
     * 丢弃策略实现
     */
    class DiscardEvictionStrategy<T> implements EvictionStrategy<T> {
        @Override
        public Event<T> resolveConflict(Event<T> newEvent, List<Event<T>> existingEvents) throws TimeLineException {
            // 直接丢弃新事件
            return null;
        }
    }
    
    /**
     * 延迟策略实现
     */
    class DelayEvictionStrategy<T> implements EvictionStrategy<T> {
        @SuppressWarnings("unchecked")
        @Override
        public Event<T> resolveConflict(Event<T> newEvent, List<Event<T>> existingEvents) throws TimeLineException {
            if (existingEvents.isEmpty()) {
                return newEvent;
            }
            
            // 找到最后一个事件
            Event<T> lastEvent = existingEvents.get(existingEvents.size() - 1);
            for (Event<T> event : existingEvents) {
                boolean isAfter = false;
                if (event.getEnd() instanceof Comparable && lastEvent.getEnd() instanceof Comparable) {
                    isAfter = ((Comparable<T>) event.getEnd()).compareTo(lastEvent.getEnd()) > 0;
                } else {
                    isAfter = event.getEnd().toString().compareTo(lastEvent.getEnd().toString()) > 0;
                }
                
                if (isAfter) {
                    lastEvent = event;
                }
            }
            
            // 将新事件安排在最后事件之后
            newEvent.setStart(lastEvent.getEnd());
            // 注意：这里需要TimeCalculator来计算结束时间
            return newEvent;
        }
    }
    
    /**
     * 获取丢弃策略实例
     * @param <T> 时间类型
     * @return 丢弃策略实例
     */
    @SuppressWarnings("unchecked")
    static <T> EvictionStrategy<T> getDiscardStrategy() {
        return (EvictionStrategy<T>) DISCARD;
    }
    
    /**
     * 获取延迟策略实例
     * @param <T> 时间类型
     * @return 延迟策略实例
     */
    @SuppressWarnings("unchecked")
    static <T> EvictionStrategy<T> getDelayStrategy() {
        return (EvictionStrategy<T>) DELAY;
    }
}