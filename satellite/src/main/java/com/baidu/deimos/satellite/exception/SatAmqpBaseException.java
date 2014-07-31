/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.exception;

/**
 * @description 通用amqp基础服务异常
 * @date Jan 15, 2014 2:25:50 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SatAmqpBaseException extends RuntimeException {

    private static final long serialVersionUID = -578012046622316765L;

    /**
     * 构造函数
     */
    public SatAmqpBaseException() {
        super();
    }

    /**
     * 构造函数
     * @param reason 异常原因
     * @param error 具体错误
     */
    public SatAmqpBaseException(String reason, Throwable error) {
        super(reason, error);
    }

}
