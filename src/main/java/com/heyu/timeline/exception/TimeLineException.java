package com.heyu.timeline.exception;

/**
 * 时间线相关的自定义异常类
 * 用于处理时间线操作中的各种异常情况
 */
public class TimeLineException extends Exception {
    
    /**
     * 构造一个没有详细信息的TimeLineException实例
     */
    public TimeLineException() {
        super();
    }
    
    /**
     * 构造一个带有指定详细信息的TimeLineException实例
     * 
     * @param message 异常的详细信息
     */
    public TimeLineException(String message) {
        super(message);
    }
    
    /**
     * 构造一个带有指定详细信息和原因的TimeLineException实例
     * 
     * @param message 异常的详细信息
     * @param cause 异常的原因
     */
    public TimeLineException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造一个带有指定原因的TimeLineException实例
     * 
     * @param cause 异常的原因
     */
    public TimeLineException(Throwable cause) {
        super(cause);
    }
}