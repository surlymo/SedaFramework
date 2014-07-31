/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.exception;

/**
 * @description ͨ��amqp���������쳣
 * @date Jan 15, 2014 2:25:50 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SatAmqpBaseException extends RuntimeException {

    private static final long serialVersionUID = -578012046622316765L;

    /**
     * ���캯��
     */
    public SatAmqpBaseException() {
        super();
    }

    /**
     * ���캯��
     * @param reason �쳣ԭ��
     * @param error �������
     */
    public SatAmqpBaseException(String reason, Throwable error) {
        super(reason, error);
    }

}
