package com.heyu.timeline.calculator;

/**
 * 简单时间计算器实现示例
 * 适用于数值型时间类型（如Integer、Long等）
 * @param <T> 数值型时间类型
 */
public class SimpleTimeCalculator<T extends Number & Comparable<T>> implements TimeCalculator<T> {
    
    @SuppressWarnings("unchecked")
    @Override
    public T add(T start, T duration) {
        if (start instanceof Integer) {
            return (T) Integer.valueOf(start.intValue() + duration.intValue());
        } else if (start instanceof Long) {
            return (T) Long.valueOf(start.longValue() + duration.longValue());
        } else if (start instanceof Double) {
            return (T) Double.valueOf(start.doubleValue() + duration.doubleValue());
        } else if (start instanceof Float) {
            return (T) Float.valueOf(start.floatValue() + duration.floatValue());
        }
        throw new UnsupportedOperationException("Unsupported number type: " + start.getClass());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T subtract(T end, T duration) {
        if (end instanceof Integer) {
            return (T) Integer.valueOf(end.intValue() - duration.intValue());
        } else if (end instanceof Long) {
            return (T) Long.valueOf(end.longValue() - duration.longValue());
        } else if (end instanceof Double) {
            return (T) Double.valueOf(end.doubleValue() - duration.doubleValue());
        } else if (end instanceof Float) {
            return (T) Float.valueOf(end.floatValue() - duration.floatValue());
        }
        throw new UnsupportedOperationException("Unsupported number type: " + end.getClass());
    }
    
    @Override
    public int compare(T t1, T t2) {
        return t1.compareTo(t2);
    }
}