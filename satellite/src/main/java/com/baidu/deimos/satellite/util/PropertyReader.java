/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.util;

import java.util.Properties;

import com.baidu.deimos.satellite.exception.SatAmqpBaseException;

/**
 * @title PropertyReader
 * @description properties�ļ�������
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class PropertyReader {

    private static Properties PRO;

    /**
     * ��ȡproperties�ļ�����
     * @param key ����key
     * @return ����ֵ
     */
    public static String getProperty(String key) {
        if (PRO == null) {
            try {
                PRO.load(PropertyReader.class.getClassLoader().getResourceAsStream("config.properties"));
            } catch (Exception e) {
                throw new SatAmqpBaseException("load properties file error.", e);
            }
        }
        return PRO.getProperty(key);
    }
}
