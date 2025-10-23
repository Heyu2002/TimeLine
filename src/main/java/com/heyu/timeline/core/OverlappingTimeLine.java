package com.heyu.timeline.core;

import com.heyu.timeline.calculator.TimeCalculator;
import com.heyu.timeline.exception.TimeLineException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 时间线数据结构，用于存储和管理可能重叠的事件
 * @param <T> 时间类型，必须实现Comparable接口
 */
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OverlappingTimeLine<T extends Comparable<T>> implements TimelineStructure<T> {

    // 使用列表存储所有事件
    private final List<Event<T>> events = new ArrayList<>();

    // 使用TreeMap按开始时间索引事件，支持同一时间点的多个事件
    private final TreeMap<T, List<Event<T>>> startTimeIndex = new TreeMap<>();

    // 使用TreeMap按结束时间索引事件
    private final TreeMap<T, List<Event<T>>> endTimeIndex = new TreeMap<>();

    // 为每个时间桶提供锁机制
    private final Map<T, Lock> startLocks = new ConcurrentHashMap<>();
    private final Map<T, Lock> endLocks = new ConcurrentHashMap<>();

    // 全局锁，用于保护events列表
    private final Lock globalLock = new ReentrantLock();
    
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
     * 添加事件到时间线
     * @param event 要添加的事件
     * @throws TimeLineException 当事件为null时抛出异常
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
            events.add(event);
        } finally {
            globalLock.unlock();
        }

        // 为开始时间获取锁
        Lock startLock = startLocks.computeIfAbsent(event.getStart(), k -> new ReentrantLock());
        startLock.lock();
        try {
            // 按开始时间索引
            startTimeIndex.computeIfAbsent(event.getStart(), k -> new ArrayList<>()).add(event);
        } finally {
            startLock.unlock();
        }

        // 为结束时间获取锁
        Lock endLock = endLocks.computeIfAbsent(event.getEnd(), k -> new ReentrantLock());
        endLock.lock();
        try {
            // 按结束时间索引
            endTimeIndex.computeIfAbsent(event.getEnd(), k -> new ArrayList<>()).add(event);
        } finally {
            endLock.unlock();
        }
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
        List<Event<T>> sortedEvents = getAllEvents();
        Collections.sort(sortedEvents);
        
        // 如果没有任何事件，从"零点"开始安排
        if (sortedEvents.isEmpty()) {
            // 这里需要根据具体的时间类型来确定"零点"，暂时抛出异常
            throw new TimeLineException("Cannot automatically determine start time for event with only duration. " +
                    "Please provide a specific start time or implement a custom TimeCalculator.");
        }
        
        // 寻找两个事件之间的空隙是否足够容纳新事件
        for (int i = 0; i < sortedEvents.size() - 1; i++) {
            Event<T> currentEvent = sortedEvents.get(i);
            Event<T> nextEvent = sortedEvents.get(i + 1);
            
            // 检查当前事件结束后到下一事件开始前是否有足够的时间
            T gapStart = currentEvent.getEnd();
            T gapEnd = nextEvent.getStart();
            
            // 计算这个时间段是否足够长来容纳新事件
            // 这需要时间计算器来完成，这里只是一个概念性实现
            throw new TimeLineException("Automatic time slot assignment requires a custom implementation " +
                    "based on the specific time type. Please provide specific start and end times.");
        }
        
        // 如果所有现有事件之后的时间段都可以容纳新事件，则安排在最后
        Event<T> lastEvent = sortedEvents.get(sortedEvents.size() - 1);
        T newStart = lastEvent.getEnd();
        T newEnd = timeCalculator.add(newStart, event.getDuration());
        event.setStart(newStart);
        event.setEnd(newEnd);
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
                    List<Event<T>> startEvents = startTimeIndex.get(event.getStart());
                    if (startEvents != null) {
                        startEvents.remove(event);
                        // 如果该时间点没有其他事件了，清理索引和锁
                        if (startEvents.isEmpty()) {
                            startTimeIndex.remove(event.getStart());
                            startLocks.remove(event.getStart());
                        }
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
                    List<Event<T>> endEvents = endTimeIndex.get(event.getEnd());
                    if (endEvents != null) {
                        endEvents.remove(event);
                        // 如果该时间点没有其他事件了，清理索引和锁
                        if (endEvents.isEmpty()) {
                            endTimeIndex.remove(event.getEnd());
                            endLocks.remove(event.getEnd());
                        }
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
            Collections.sort(sortedEvents);
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
            SortedMap<T, List<Event<T>>> headMap = startTimeIndex.headMap(time, true);
            for (Map.Entry<T, List<Event<T>>> entry : headMap.entrySet()) {
                Lock lock = startLocks.get(entry.getKey());
                if (lock != null) {
                    lock.lock();
                    try {
                        for (Event<T> event : entry.getValue()) {
                            // 确保事件在指定时间仍然活跃（结束时间大于等于指定时间）且事件本身是活跃的
                            if (event.getEnd().compareTo(time) >= 0 && event.isActive()) {
                                result.add(event);
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
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

        if (start.compareTo(end) > 0) {
            throw new TimeLineException("Start time cannot be after end time");
        }

        List<Event<T>> result = new ArrayList<>();
        Set<Event<T>> uniqueEvents = new HashSet<>();

        // 查找开始时间在指定时间段之前或之内的事件
        globalLock.lock();
        try {
            SortedMap<T, List<Event<T>>> headMap = startTimeIndex.headMap(end, true);
            for (Map.Entry<T, List<Event<T>>> entry : headMap.entrySet()) {
                Lock lock = startLocks.get(entry.getKey());
                if (lock != null) {
                    lock.lock();
                    try {
                        for (Event<T> event : entry.getValue()) {
                            // 确保事件与指定时间段有重叠且事件本身是活跃的
                            if (event.getEnd().compareTo(start) >= 0 && event.getStart().compareTo(end) <= 0 && event.isActive()) {
                                if (uniqueEvents.add(event)) {
                                    result.add(event);
                                }
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        } finally {
            globalLock.unlock();
        }

        // 按时间顺序排序
        Collections.sort(result);
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