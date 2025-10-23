package com.heyu.timeline.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时间线事件
 * @param <T> 用来判断先后所需要的事件类型，需要实现comparable接口
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event<T extends Comparable<T>> implements Comparable<Event<T>>{

    /**
     * 事件的起始时间
     */
    private T start;
    /**
     * 事件的结束时间
     */
    private T end;
    
    /**
     * 事件的持续时间
     */
    private T duration;

    /**
     * 事件的内容主体
     */
    private Object subject;
    
    /**
     * 事件的活跃状态，默认为true（活跃）
     */
    private boolean active = true;

    /**
     * 构造函数，用于创建具有明确开始和结束时间的事件
     * @param start 开始时间
     * @param end 结束时间
     * @param subject 事件主体
     */
    public Event(T start, T end, Object subject) {
        this.start = start;
        this.end = end;
        this.subject = subject;
        this.active = true;
    }
    
    /**
     * 构造函数，用于创建只有持续时间的事件
     * @param duration 持续时间
     * @param subject 事件主体
     */
    public Event(T duration, Object subject) {
        this.duration = duration;
        this.subject = subject;
        this.active = true;
    }

    /**
     * 设置事件为活跃状态
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * 设置事件为非活跃状态
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * 判断事件是否活跃
     * @return 如果事件活跃返回true，否则返回false
     */
    public boolean isActive() {
        return this.active;
    }
    
    /**
     * 判断事件是否只有持续时间而没有明确的开始和结束时间
     * @return 如果只有持续时间返回true，否则返回false
     */
    public boolean hasOnlyDuration() {
        return (this.start == null || this.end == null) && this.duration != null;
    }

    @Override
    public int compareTo(Event<T> o) {
        // 如果任一事件非活跃，只比较时间，不考虑活跃状态
        // 首先比较开始时间
        if (this.start != null && o.start != null) {
            int startComparison = this.start.compareTo(o.start);
            if (startComparison != 0) {
                return startComparison;
            }
        }
        
        // 开始时间相同时比较结束时间
        if (this.end != null && o.end != null) {
            int endComparison = this.end.compareTo(o.end);
            if (endComparison != 0) {
                return endComparison;
            }
        }
        
        // 时间完全相同时，活跃事件排在非活跃事件前面
        if (this.active && !o.active) {
            return -1;
        }
        if (!this.active && o.active) {
            return 1;
        }
        
        // 都活跃或都不活跃，视为相等
        return 0;
    }
}