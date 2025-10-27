package com.heyu.timeline.calculator;

/**
 * 时间计算器接口，用于处理时间类型的加减运算
 * @param <T> 时间类型
 */
public interface TimeCalculator<T> {
    
    /**
     * 计算开始时间加上持续时间后的结束时间
     * @param start 开始时间
     * @param duration 持续时间
     * @return 结束时间
     */
    T add(T start, T duration);
    
    /**
     * 计算结束时间减去持续时间后的开始时间
     * @param end 结束时间
     * @param duration 持续时间
     * @return 开始时间
     */
    T subtract(T end, T duration);
    
    /**
     * 比较两个时间的差异
     * @param t1 时间1
     * @param t2 时间2
     * @return 时间差异
     */
    int compare(T t1, T t2);
    
    /**
     * 获取零点时间（时间轴的起点）
     * @return 零点时间
     */
    T getZero();
}