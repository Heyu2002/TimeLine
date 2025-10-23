package com.heyu.timeline.core;

import com.heyu.timeline.calculator.TimeCalculator;
import com.heyu.timeline.exception.TimeLineException;

import java.util.List;

/**
 * 时间线结构接口，定义了时间线的基本操作
 * @param <T> 时间类型，必须实现Comparable接口
 */
public interface TimelineStructure<T extends Comparable<T>> {
    
    /**
     * 设置时间计算器
     * @param timeCalculator 时间计算器
     */
    void setTimeCalculator(TimeCalculator<T> timeCalculator);
    
    /**
     * 添加事件到时间线
     * @param event 要添加的事件
     * @throws TimeLineException 当事件为null时抛出异常
     */
    void addEvent(Event<T> event) throws TimeLineException;
    
    /**
     * 从时间线中移除指定事件
     * @param event 要移除的事件
     * @return 如果成功移除返回true，否则返回false
     * @throws TimeLineException 当事件为null时抛出异常
     */
    boolean removeEvent(Event<T> event) throws TimeLineException;
    
    /**
     * 根据事件的开始和结束时间移除事件
     * @param start 事件开始时间
     * @param end 事件结束时间
     * @param subject 事件主体
     * @return 如果成功移除返回true，否则返回false
     * @throws TimeLineException 当时间参数为null时抛出异常
     */
    boolean removeEvent(T start, T end, Object subject) throws TimeLineException;
    
    /**
     * 获取按时间顺序排列的所有活跃事件
     * @return 排序后的活跃事件列表
     */
    List<Event<T>> getSortedEvents();
    
    /**
     * 获取在指定时间点活跃的所有事件
     * @param time 时间点
     * @return 在该时间点活跃的事件列表
     * @throws TimeLineException 当时间参数为null时抛出异常
     */
    List<Event<T>> getEventsAt(T time) throws TimeLineException;
    
    /**
     * 获取在指定时间段内活跃的所有事件
     * @param start 开始时间
     * @param end 结束时间
     * @return 在该时间段内活跃的事件列表
     * @throws TimeLineException 当时间参数为null时抛出异常
     */
    List<Event<T>> getEventsBetween(T start, T end) throws TimeLineException;
    
    /**
     * 移除所有非活跃事件
     * @return 被移除的事件数量
     */
    int removeInactiveEvents();
    
    /**
     * 获取所有事件（包括非活跃事件）
     * @return 所有事件的列表
     */
    List<Event<T>> getAllEvents();
    
    /**
     * 获取所有非活跃事件
     * @return 非活跃事件的列表
     */
    List<Event<T>> getInactiveEvents();
    
    /**
     * 清空所有事件
     */
    void clear();
}