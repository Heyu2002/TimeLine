package com.heyu.timeline.calculator;

/**
 * 时间计算器工厂类
 * 根据时间类型创建相应的时间计算器
 */
public class TimeCalculatorFactory {
    
    /**
     * 根据时间类型创建时间计算器
     * @param timeClass 时间类型Class
     * @param <T> 时间类型
     * @return 时间计算器
     */
    @SuppressWarnings("unused")
    public static <T> TimeCalculator<T> createTimeCalculator(Class<T> timeClass) {
        return new GenericTimeCalculator<>(timeClass);
    }
}