package com.heyu.timeline.factory;

import com.heyu.timeline.core.timeline.OverlappingTimeLine;
import com.heyu.timeline.core.timeline.TimeLine;
import com.heyu.timeline.exception.TimeLineException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间线池，防止创建过多时间线导致的系统问题，时间线的重复问题等等
 */
public class TimeLinePool {
    
    // 默认最大时间线数量
    private static int maxTimeLines = 3;
    
    // 当前时间线数量计数器
    private static final AtomicInteger currentTimeLineCount = new AtomicInteger(0);
    
    private static final Map<String, Object> timeLineMap = new ConcurrentHashMap<>();
    
    /**
     * 设置最大时间线数量
     * @param max 最大时间线数量
     */
    public static void setMaxTimeLines(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Maximum number of timelines must be positive");
        }
        maxTimeLines = max;
    }
    
    /**
     * 获取最大时间线数量
     * @return 最大时间线数量
     */
    public static int getMaxTimeLines() {
        return maxTimeLines;
    }
    
    /**
     * 获取当前时间线数量
     * @return 当前时间线数量
     */
    public static int getCurrentTimeLineCount() {
        return currentTimeLineCount.get();
    }
    
    /**
     * 获取指定名称的OverlappingTimeLine时间线
     * @param name 时间线名称
     * @param <T> 时间类型
     * @return 时间线实例
     * @throws TimeLineException 当时间线数量达到上限时抛出异常
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> OverlappingTimeLine<T> getOverlappingTimeLine(String name) throws TimeLineException {
        // 检查是否已存在该名称的时间线
        Object existingTimeLine = timeLineMap.get(name);
        if (existingTimeLine instanceof OverlappingTimeLine) {
            return (OverlappingTimeLine<T>) existingTimeLine;
        }
        
        // 检查是否达到最大时间线数量限制
        if (currentTimeLineCount.get() >= maxTimeLines) {
            throw new TimeLineException("Maximum number of timelines (" + maxTimeLines + ") reached. " +
                    "Current count: " + currentTimeLineCount.get());
        }
        
        // 创建新的时间线并增加计数
        OverlappingTimeLine<T> newTimeLine = new OverlappingTimeLine<>();
        timeLineMap.put(name, newTimeLine);
        currentTimeLineCount.incrementAndGet();
        return newTimeLine;
    }
    
    /**
     * 获取指定名称的TimeLine时间线
     * @param name 时间线名称
     * @param <T> 时间类型
     * @return 时间线实例
     * @throws TimeLineException 当时间线数量达到上限时抛出异常
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> TimeLine<T> getTimeLine(String name) throws TimeLineException {
        // 检查是否已存在该名称的时间线
        Object existingTimeLine = timeLineMap.get(name);
        if (existingTimeLine instanceof TimeLine) {
            return (TimeLine<T>) existingTimeLine;
        }
        
        // 检查是否达到最大时间线数量限制
        if (currentTimeLineCount.get() >= maxTimeLines) {
            throw new TimeLineException("Maximum number of timelines (" + maxTimeLines + ") reached. " +
                    "Current count: " + currentTimeLineCount.get());
        }
        
        // 创建新的时间线并增加计数
        TimeLine<T> newTimeLine = new TimeLine<>();
        timeLineMap.put(name, newTimeLine);
        currentTimeLineCount.incrementAndGet();
        return newTimeLine;
    }
    
    /**
     * 移除指定名称的时间线
     * @param name 时间线名称
     */
    public static void removeTimeLine(String name) {
        Object removed = timeLineMap.remove(name);
        if (removed != null) {
            currentTimeLineCount.decrementAndGet();
        }
    }
    
    /**
     * 清空时间线池
     */
    public static void clear() {
        timeLineMap.clear();
        currentTimeLineCount.set(0);
    }
}