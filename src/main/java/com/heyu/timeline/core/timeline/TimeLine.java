package com.heyu.timeline.core.timeline;

import com.heyu.timeline.core.model.Event;
import com.heyu.timeline.core.strategy.EvictionStrategy;
import com.heyu.timeline.exception.TimeLineException;
import com.heyu.timeline.calculator.TimeCalculator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 时间线类，事件不能重叠
 * 如果有重叠事件，根据淘汰策略决定是插入到后续时间中还是直接抛弃
 * @param <T> 时间类型
 */
public class TimeLine<T> implements TimelineStructure<T> {
    
    // 使用列表存储所有事件
    private final List<Event<T>> events = new ArrayList<>();
    
    // 使用TreeMap按开始时间索引事件，支持同一时间点的多个事件
    private final TreeMap<T, Event<T>> startTimeIndex = new TreeMap<>(new Comparator<T>() {
        @SuppressWarnings("unchecked")
        @Override
        public int compare(T o1, T o2) {
            if (o1 instanceof Comparable && o2 instanceof Comparable) {
                return ((Comparable<T>) o1).compareTo(o2);
            }
            // 如果类型不可比较，使用toString进行比较
            return o1.toString().compareTo(o2.toString());
        }
    });

    // 使用TreeMap按结束时间索引事件
    private final TreeMap<T, Event<T>> endTimeIndex = new TreeMap<>(new Comparator<T>() {
        @SuppressWarnings("unchecked")
        @Override
        public int compare(T o1, T o2) {
            if (o1 instanceof Comparable && o2 instanceof Comparable) {
                return ((Comparable<T>) o1).compareTo(o2);
            }
            // 如果类型不可比较，使用toString进行比较
            return o1.toString().compareTo(o2.toString());
        }
    });
    
    // 为每个时间桶提供锁机制
    private final Map<T, Lock> startLocks = new ConcurrentHashMap<>();
    private final Map<T, Lock> endLocks = new ConcurrentHashMap<>();
    
    // 全局锁，用于保护events列表
    private final Lock globalLock = new ReentrantLock();
    
    // 淘汰策略
    private EvictionStrategy<T> evictionStrategy = EvictionStrategy.getDiscardStrategy();
    
    // 时间计算器，用于处理时间类型的加减运算
    private TimeCalculator<T> timeCalculator;
    
    /**
     * 设置时间计算器
     * @param timeCalculator 时间计算器
     */
    public void setTimeCalculator(TimeCalculator<T> timeCalculator) {
        this.timeCalculator = timeCalculator;
    }
    
    /**
     * 设置淘汰策略
     * @param evictionStrategy 淘汰策略
     */
    public void setEvictionStrategy(EvictionStrategy<T> evictionStrategy) {
        if (evictionStrategy == null) {
            throw new IllegalArgumentException("Eviction strategy cannot be null");
        }
        this.evictionStrategy = evictionStrategy;
    }
    
    /**
     * 获取当前淘汰策略
     * @return 淘汰策略
     */
    public EvictionStrategy<T> getEvictionStrategy() {
        return evictionStrategy;
    }
    
    /**
     * 添加事件到时间线
     * @param event 要添加的事件
     * @throws TimeLineException 当事件为null或发生冲突时抛出异常
     */
    public void addEvent(Event<T> event) throws TimeLineException {
        if (event == null) {
            throw new TimeLineException("Cannot add null event to timeline");
        }
        
        // 如果事件只有持续时间而没有明确的开始和结束时间，则寻找合适的时间段
        if (event.hasOnlyDuration()) {
            assignTimeSlot(event);
        }
        
        globalLock.lock();
        try {
            // 检查是否有重叠
            if (hasOverlap(event)) {
                // 根据淘汰策略处理冲突
                Event<T> resolvedEvent = evictionStrategy.resolveConflict(event, new ArrayList<>(events));
                if (resolvedEvent == null) {
                    // 事件被丢弃
                    return;
                } else {
                    // 使用解决冲突后的事件
                    event = resolvedEvent;
                }
            }
            
            events.add(event);
            
            // 为开始时间获取锁
            Lock startLock = startLocks.computeIfAbsent(event.getStart(), k -> new ReentrantLock());
            startLock.lock();
            try {
                // 按开始时间索引
                startTimeIndex.put(event.getStart(), event);
            } finally {
                startLock.unlock();
            }
            
            // 为结束时间获取锁
            Lock endLock = endLocks.computeIfAbsent(event.getEnd(), k -> new ReentrantLock());
            endLock.lock();
            try {
                // 按结束时间索引
                endTimeIndex.put(event.getEnd(), event);
            } finally {
                endLock.unlock();
            }
        } finally {
            globalLock.unlock();
        }
    }
    
    /**
     * 检查事件是否与其他事件重叠
     * @param event 要检查的事件
     * @return 如果有重叠返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    private boolean hasOverlap(Event<T> event) {
        // 查找开始时间小于等于指定事件结束时间的所有事件
        SortedMap<T, Event<T>> headMap = startTimeIndex.headMap(event.getEnd(), true);
        for (Event<T> existingEvent : headMap.values()) {
            // 确保事件在指定时间仍然活跃（结束时间大于等于指定事件开始时间）
            boolean isOverlapping = false;
            if (existingEvent.getEnd() instanceof Comparable && event.getStart() instanceof Comparable) {
                isOverlapping = ((Comparable<T>) existingEvent.getEnd()).compareTo(event.getStart()) >= 0;
            } else {
                isOverlapping = existingEvent.getEnd().toString().compareTo(event.getStart().toString()) >= 0;
            }
            if (isOverlapping) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 为只有持续时间的事件寻找合适的时间段
     * @param event 只有持续时间的事件
     * @throws TimeLineException 当无法找到合适的时间段或缺少时间计算器时抛出异常
     */
    private void assignTimeSlot(Event<T> event) throws TimeLineException {
        if (event.getDuration() == null) {
            throw new TimeLineException("Event must have a duration to be scheduled");
        }
        
        if (timeCalculator == null) {
            throw new TimeLineException("TimeCalculator is required to schedule events with only duration. " +
                    "Please set a TimeCalculator using setTimeCalculator method.");
        }
        
        // 如果已经有开始或结束时间，则不需要安排
        if (event.getStart() != null || event.getEnd() != null) {
            return;
        }
        
        // 寻找第一个可以容纳该事件的时间段
        findAndAssignTimeSlot(event);
    }
    
    /**
     * 寻找并分配时间段给事件
     * @param event 需要分配时间段的事件
     * @throws TimeLineException 当无法找到合适的时间段时抛出异常
     */
    private void findAndAssignTimeSlot(Event<T> event) throws TimeLineException {
        // 获取所有已排序的事件
        List<Event<T>> sortedEvents = new ArrayList<>(events);
        sortEvents(sortedEvents);
        
        // 如果没有任何事件，从"零点"开始安排
        if (sortedEvents.isEmpty()) {
            // 使用时间计算器创建零点和结束时间
            T zeroTime = getZeroTime();
            T endTime = timeCalculator.add(zeroTime, event.getDuration());
            event.setStart(zeroTime);
            event.setEnd(endTime);
            return;
        }
        
        // 寻找两个事件之间的空隙是否足够容纳新事件
        for (int i = 0; i < sortedEvents.size() - 1; i++) {
            Event<T> currentEvent = sortedEvents.get(i);
            Event<T> nextEvent = sortedEvents.get(i + 1);
            
            // 检查当前事件结束后到下一事件开始前是否有足够的时间
            T gapStart = currentEvent.getEnd();
            T gapEnd = nextEvent.getStart();
            
            if (canFitInGap(gapStart, gapEnd, event.getDuration())) {
                T startTime = gapStart;
                T endTime = timeCalculator.add(startTime, event.getDuration());
                event.setStart(startTime);
                event.setEnd(endTime);
                return;
            }
        }
        
        // 如果所有现有事件之后的时间段都可以容纳新事件，则安排在最后
        Event<T> lastEvent = sortedEvents.get(sortedEvents.size() - 1);
        T newStart = lastEvent.getEnd();
        T newEnd = timeCalculator.add(newStart, event.getDuration());
        event.setStart(newStart);
        event.setEnd(newEnd);
    }
    
    /**
     * 获取零点时间
     * @return 零点时间
     */
    private T getZeroTime() throws TimeLineException {
        // 这里根据具体的时间类型来确定"零点"
        if (timeCalculator != null) {
            try {
                return timeCalculator.getZero();
            } catch (UnsupportedOperationException e) {
                // 如果时间计算器不支持获取零点，则抛出自定义异常
                throw new TimeLineException("TimeCalculator does not support getting zero time. " +
                        "Please provide a TimeCalculator implementation that supports getZero() method.");
            }
        }
        // 如果没有设置时间计算器，抛出异常
        throw new TimeLineException("Cannot automatically determine zero time without a TimeCalculator. " +
                "Please set a TimeCalculator using setTimeCalculator method.");
    }
    
    /**
     * 检查事件是否可以放在两个事件之间的间隙中
     * @param gapStart 间隙开始时间
     * @param gapEnd 间隙结束时间
     * @param duration 持续时间
     * @return 是否可以放置
     */
    private boolean canFitInGap(T gapStart, T gapEnd, T duration) {
        // 计算间隙的持续时间
        T gapDuration;
        try {
            gapDuration = timeCalculator.subtract(gapEnd, gapStart);
        } catch (UnsupportedOperationException e) {
            // 如果不支持减法运算，则无法计算间隙大小
            return false;
        }
        
        // 比较间隙持续时间和事件持续时间
        return timeCalculator.compare(gapDuration, duration) >= 0;
    }
    
    /**
     * 对事件列表进行排序
     * @param events 事件列表
     */
    @SuppressWarnings("unchecked")
    private void sortEvents(List<Event<T>> events) {
        Collections.sort(events, new Comparator<Event<T>>() {
            @Override
            public int compare(Event<T> o1, Event<T> o2) {
                // 首先比较开始时间
                if (o1.getStart() != null && o2.getStart() != null) {
                    if (o1.getStart() instanceof Comparable && o2.getStart() instanceof Comparable) {
                        int startComparison = ((Comparable<T>) o1.getStart()).compareTo(o2.getStart());
                        if (startComparison != 0) {
                            return startComparison;
                        }
                    } else {
                        int startComparison = o1.getStart().toString().compareTo(o2.getStart().toString());
                        if (startComparison != 0) {
                            return startComparison;
                        }
                    }
                }
                
                // 开始时间相同时比较结束时间
                if (o1.getEnd() != null && o2.getEnd() != null) {
                    if (o1.getEnd() instanceof Comparable && o2.getEnd() instanceof Comparable) {
                        int endComparison = ((Comparable<T>) o1.getEnd()).compareTo(o2.getEnd());
                        if (endComparison != 0) {
                            return endComparison;
                        }
                    } else {
                        int endComparison = o1.getEnd().toString().compareTo(o2.getEnd().toString());
                        if (endComparison != 0) {
                            return endComparison;
                        }
                    }
                }
                
                // 时间完全相同时，活跃事件排在非活跃事件前面
                if (o1.isActive() && !o2.isActive()) {
                    return -1;
                }
                if (!o1.isActive() && o2.isActive()) {
                    return 1;
                }
                
                // 都活跃或都不活跃，视为相等
                return 0;
            }
        });
    }
    
    /**
     * 从时间线中移除指定事件
     * @param event 要移除的事件
     * @return 如果成功移除返回true，否则返回false
     * @throws TimeLineException 当事件为null时抛出异常
     */
    public boolean removeEvent(Event<T> event) throws TimeLineException {
        if (event == null) {
            throw new TimeLineException("Cannot remove null event from timeline");
        }
        
        globalLock.lock();
        try {
            // 从全局事件列表中移除
            boolean removed = events.remove(event);
            if (!removed) {
                return false; // 事件不存在
            }
            
            // 从开始时间索引中移除
            Lock startLock = startLocks.get(event.getStart());
            if (startLock != null) {
                startLock.lock();
                try {
                    startTimeIndex.remove(event.getStart());
                    // 如果该时间点没有其他事件了，清理索引和锁
                    if (!startTimeIndex.containsKey(event.getStart())) {
                        startLocks.remove(event.getStart());
                    }
                } finally {
                    startLock.unlock();
                }
            }
            
            // 从结束时间索引中移除
            Lock endLock = endLocks.get(event.getEnd());
            if (endLock != null) {
                endLock.lock();
                try {
                    endTimeIndex.remove(event.getEnd());
                    // 如果该时间点没有其他事件了，清理索引和锁
                    if (!endTimeIndex.containsKey(event.getEnd())) {
                        endLocks.remove(event.getEnd());
                    }
                } finally {
                    endLock.unlock();
                }
            }
            
            return true;
        } finally {
            globalLock.unlock();
        }
    }
    
    /**
     * 根据事件的开始和结束时间移除事件
     * @param start 事件开始时间
     * @param end 事件结束时间
     * @param subject 事件主体
     * @return 如果成功移除返回true，否则返回false
     * @throws TimeLineException 当时间参数为null时抛出异常
     */
    public boolean removeEvent(T start, T end, Object subject) throws TimeLineException {
        if (start == null || end == null) {
            throw new TimeLineException("Start time and end time cannot be null");
        }
        
        globalLock.lock();
        try {
            Event<T> eventToRemove = null;
            // 在全局事件列表中查找匹配的事件
            for (Event<T> event : events) {
                if (event.getStart().equals(start) &&
                        event.getEnd().equals(end) &&
                        Objects.equals(event.getSubject(), subject)) {
                    eventToRemove = event;
                    break;
                }
            }
            
            if (eventToRemove != null) {
                return removeEvent(eventToRemove);
            }
            
            return false;
        } finally {
            globalLock.unlock();
        }
    }
    
    /**
     * 获取按时间顺序排列的所有活跃事件
     * @return 排序后的活跃事件列表
     */
    public List<Event<T>> getSortedEvents() {
        globalLock.lock();
        try {
            List<Event<T>> sortedEvents = new ArrayList<>();
            for (Event<T> event : events) {
                if (event.isActive()) {
                    sortedEvents.add(event);
                }
            }
            sortEvents(sortedEvents);
            return sortedEvents;
        } finally {
            globalLock.unlock();
        }
    }
    
    /**
     * 获取在指定时间点活跃的所有事件
     * @param time 时间点
     * @return 在该时间点活跃的事件列表
     * @throws TimeLineException 当时间参数为null时抛出异常
     */
    public List<Event<T>> getEventsAt(T time) throws TimeLineException {
        if (time == null) {
            throw new TimeLineException("Time cannot be null");
        }
        
        List<Event<T>> result = new ArrayList<>();
        
        // 查找开始时间小于等于指定时间的所有事件
        globalLock.lock();
        try {
            SortedMap<T, Event<T>> headMap = startTimeIndex.headMap(time, true);
            for (Event<T> event : headMap.values()) {
                // 确保事件在指定时间仍然活跃（结束时间大于等于指定时间）且事件本身是活跃的
                boolean isActiveAtTime = false;
                if (event.getEnd() instanceof Comparable && time instanceof Comparable) {
                    isActiveAtTime = ((Comparable<T>) event.getEnd()).compareTo(time) >= 0 && event.isActive();
                } else {
                    isActiveAtTime = event.getEnd().toString().compareTo(time.toString()) >= 0 && event.isActive();
                }
                if (isActiveAtTime) {
                    result.add(event);
                }
            }
        } finally {
            globalLock.unlock();
        }
        
        return result;
    }
    
    /**
     * 获取在指定时间段内活跃的所有事件
     * @param start 开始时间
     * @param end 结束时间
     * @return 在该时间段内活跃的事件列表
     * @throws TimeLineException 当时间参数为null时抛出异常
     */
    public List<Event<T>> getEventsBetween(T start, T end) throws TimeLineException {
        if (start == null || end == null) {
            throw new TimeLineException("Start time and end time cannot be null");
        }

        // 注意：这里需要比较时间，但T类型可能不可比较
        boolean isStartAfterEnd = false;
        if (start instanceof Comparable && end instanceof Comparable) {
            isStartAfterEnd = ((Comparable<T>) start).compareTo(end) > 0;
        } else {
            isStartAfterEnd = start.toString().compareTo(end.toString()) > 0;
        }
        
        if (isStartAfterEnd) {
            throw new TimeLineException("Start time cannot be after end time");
        }
        
        List<Event<T>> result = new ArrayList<>();
        
        // 查找开始时间在指定时间段之前或之内的事件
        globalLock.lock();
        try {
            SortedMap<T, Event<T>> headMap = startTimeIndex.headMap(end, true);
            for (Event<T> event : headMap.values()) {
                // 确保事件与指定时间段有重叠且事件本身是活跃的
                // 需要检查event.getEnd() >= start && event.getStart() <= end
                boolean isOverlapping = false;
                if (event.getEnd() instanceof Comparable && start instanceof Comparable && 
                    event.getStart() instanceof Comparable && end instanceof Comparable) {
                    boolean endAfterStart = ((Comparable<T>) event.getEnd()).compareTo(start) >= 0;
                    boolean startBeforeEnd = ((Comparable<T>) event.getStart()).compareTo(end) <= 0;
                    isOverlapping = endAfterStart && startBeforeEnd && event.isActive();
                } else {
                    boolean endAfterStart = event.getEnd().toString().compareTo(start.toString()) >= 0;
                    boolean startBeforeEnd = event.getStart().toString().compareTo(end.toString()) <= 0;
                    isOverlapping = endAfterStart && startBeforeEnd && event.isActive();
                }
                
                if (isOverlapping) {
                    result.add(event);
                }
            }
        } finally {
            globalLock.unlock();
        }
        
        // 按时间顺序排序
        sortEvents(result);
        return result;
    }
    
    /**
     * 移除所有非活跃事件
     * @return 被移除的事件数量
     */
    public int removeInactiveEvents() {
        globalLock.lock();
        try {
            List<Event<T>> inactiveEvents = getInactiveEvents();
            int count = 0;
            for (Event<T> event : inactiveEvents) {
                try {
                    if (removeEvent(event)) {
                        count++;
                    }
                } catch (TimeLineException e) {
                    // 忽略异常，继续处理其他事件
                }
            }
            return count;
        } finally {
            globalLock.unlock();
        }
    }
    
    /**
     * 获取所有事件（包括非活跃事件）
     * @return 所有事件的列表
     */
    public List<Event<T>> getAllEvents() {
        globalLock.lock();
        try {
            return new ArrayList<>(events);
        } finally {
            globalLock.unlock();
        }
    }
    
    /**
     * 获取所有非活跃事件
     * @return 非活跃事件的列表
     */
    public List<Event<T>> getInactiveEvents() {
        globalLock.lock();
        try {
            List<Event<T>> inactiveEvents = new ArrayList<>();
            for (Event<T> event : events) {
                if (!event.isActive()) {
                    inactiveEvents.add(event);
                }
            }
            return inactiveEvents;
        } finally {
            globalLock.unlock();
        }
    }
    
    /**
     * 清空所有事件
     */
    public void clear() {
        globalLock.lock();
        try {
            events.clear();
            startTimeIndex.clear();
            endTimeIndex.clear();
            startLocks.clear();
            endLocks.clear();
        } finally {
            globalLock.unlock();
        }
    }
}