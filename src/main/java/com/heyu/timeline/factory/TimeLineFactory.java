package com.heyu.timeline.factory;

import com.heyu.timeline.core.OverlappingTimeLine;
import com.heyu.timeline.core.TimeLine;

/**
 * 时间线工厂类
 */
public class TimeLineFactory {
    
    /**
     * 创建一个新的OverlappingTimeLine实例
     * @param <T> 时间类型
     * @return 新的OverlappingTimeLine实例
     */
    public static <T extends Comparable<T>> OverlappingTimeLine<T> createOverlappingTimeLine() {
        return new OverlappingTimeLine<>();
    }
    
    /**
     * 创建一个新的TimeLine实例
     * @param <T> 时间类型
     * @return 新的TimeLine实例
     */
    public static <T extends Comparable<T>> TimeLine<T> createTimeLine() {
        return new TimeLine<>();
    }
}