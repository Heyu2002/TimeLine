package com.heyu.timeline.calculator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 通用时间计算器实现
 * 支持多种时间类型
 * @param <T> 时间类型
 */
public class GenericTimeCalculator<T> implements TimeCalculator<T> {
    
    private final Class<T> type;
    
    public GenericTimeCalculator(Class<T> type) {
        this.type = type;
    }
    
    @Override
    public T add(T start, T duration) {
        if (isType(Integer.class, start, duration)) {
            Integer result = (Integer) start + (Integer) duration;
            return type.cast(result);
        } else if (isType(Long.class, start, duration)) {
            Long result = (Long) start + (Long) duration;
            return type.cast(result);
        } else if (isType(Date.class, start, duration)) {
            // 对于Date类型，我们将duration的毫秒数视为要增加的时间
            Date result = new Date(((Date) start).getTime() + ((Date) duration).getTime());
            return type.cast(result);
        } else if (isType(LocalDateTime.class, start, duration)) {
            // 对于LocalDateTime类型，我们将duration视为从零点开始的时间段
            Duration dur = Duration.between(LocalDateTime.of(2000, 1, 1, 0, 0), (LocalDateTime) duration);
            LocalDateTime result = ((LocalDateTime) start).plus(dur);
            return type.cast(result);
        } else if (isType(Duration.class, start, duration)) {
            Duration result = ((Duration) start).plus((Duration) duration);
            return type.cast(result);
        }
        throw new UnsupportedOperationException("Unsupported operation for type: " + type.getName());
    }
    
    @Override
    public T subtract(T end, T duration) {
        if (isType(Integer.class, end, duration)) {
            Integer result = (Integer) end - (Integer) duration;
            return type.cast(result);
        } else if (isType(Long.class, end, duration)) {
            Long result = (Long) end - (Long) duration;
            return type.cast(result);
        } else if (isType(Date.class, end, duration)) {
            // 对于Date类型，我们将duration的毫秒数视为要减少的时间
            Date result = new Date(((Date) end).getTime() - ((Date) duration).getTime());
            return type.cast(result);
        } else if (isType(LocalDateTime.class, end, duration)) {
            // 对于LocalDateTime类型，我们将duration视为从零点开始的时间段
            Duration dur = Duration.between(LocalDateTime.of(2000, 1, 1, 0, 0), (LocalDateTime) duration);
            LocalDateTime result = ((LocalDateTime) end).minus(dur);
            return type.cast(result);
        } else if (isType(Duration.class, end, duration)) {
            Duration result = ((Duration) end).minus((Duration) duration);
            return type.cast(result);
        }
        throw new UnsupportedOperationException("Unsupported operation for type: " + type.getName());
    }
    
    @Override
    public int compare(T t1, T t2) {
        if (t1 instanceof Comparable && t2 instanceof Comparable) {
            return ((Comparable<T>) t1).compareTo(t2);
        }
        // 如果类型不可比较，使用toString进行比较
        return t1.toString().compareTo(t2.toString());
    }
    
    @Override
    public T getZero() {
        if (isType(Integer.class)) {
            return type.cast(0);
        } else if (isType(Long.class)) {
            return type.cast(0L);
        } else if (isType(Date.class)) {
            // Date的零点是1970年1月1日 00:00:00 GMT
            return type.cast(new Date(0));
        } else if (isType(LocalDateTime.class)) {
            // 使用一个固定的起始时间作为"零点"
            return type.cast(LocalDateTime.of(2000, 1, 1, 0, 0));
        } else if (isType(Duration.class)) {
            return type.cast(Duration.ZERO);
        }
        throw new UnsupportedOperationException("Unsupported zero value for type: " + type.getName());
    }
    
    /**
     * 检查类型是否匹配
     * @param targetType 目标类型
     * @param objects 要检查的对象
     * @return 是否匹配
     */
    private boolean isType(Class<?> targetType, T... objects) {
        if (!type.equals(targetType)) {
            return false;
        }
        
        if (objects != null) {
            for (T obj : objects) {
                if (obj != null && !targetType.isInstance(obj)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 检查类型是否匹配（无参版本）
     * @param targetType 目标类型
     * @return 是否匹配
     */
    private boolean isType(Class<?> targetType) {
        return type.equals(targetType);
    }
}