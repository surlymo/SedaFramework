/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.workflow.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.api.constant.Infotype;
import com.baidu.deimos.satellite.api.constant.SatApiConstant;
import com.baidu.deimos.satellite.bo.SystemAlarmConfig;
import com.baidu.deimos.satellite.constant.AlarmCode;
import com.baidu.deimos.satellite.core.SatAmqpBundle;
import com.baidu.deimos.satellite.exception.SatAmqpException;
import com.baidu.deimos.satellite.util.PropertyReader;

/**
 * @title SystemAlarmInfoBundle
 * @description ������Ϣ�ּ����error log���������쳣�������̳߳����󱻾ܾ�������STP���������������У��
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Jan 15, 2014 2:35:32 PM
 * @version 1.0
 */
public class SystemAlarmInfoBundle extends SatAmqpBundle {

    @SuppressWarnings({ "unchecked" })
    @Override
    public Object doWork(List<DeimosSatelliteRequest> msgList) throws SatAmqpException {
        // ����У�飬���Ϊ�ջ�null��ֱ�ӷ��أ����κ����Ὣnull�����ų���
        if (msgList == null || msgList.isEmpty()) {
            return null;
        }
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        for (DeimosSatelliteRequest metaMsg : msgList) {
            // ϵͳ����ϼ�
            List<SystemAlarmConfig> sysAlarmCollection = fillInitData();
            // ��ʼ�ּ�
            for (DeimosSatelliteRequest meta : (List<DeimosSatelliteRequest>) metaMsg.getRealData()) {
                // �������ö�Ӧ�Ķ��н������
                for (SystemAlarmConfig config : sysAlarmCollection) {
                    // ����¼���ܴ��������
                    if (Infotype.INFOTYPE_ALARM_ALL.equals(config.getInfotype())) {
                        config.getList().add(meta);
                        continue;// ��ֹ�ظ���
                    }
                    if (config.getInfotype().equals(meta.getInfoType())) {
                        config.getList().add(meta);
                    }
                }
            }
            // ��cache����д��ǰ��Ӧ�õĴ������Ƚ��Ƿ񳬹�ÿ��������ֵ��
            // �˴�mock����Ϊ��������
            try {
                Thread.sleep(30);
            } catch (Exception e) {
                throw new RuntimeException("mock biz costing time error!");
            }
            // ���г����жϡ��Գ��ȳ��������ж��У�ȡ����Ӧ������Ϣ��
            // �����ʼ��Ͷ������ͣ�һ������ʮ��
            String rst = contentFiller(sysAlarmCollection);
            // �˴���ϵͳ�������������͸��ͻ�������û�и����DB������
            // ������Ҫ���͸��ͻ�����ͨ�����ĵķ�ʽ��
            // ���û����������Ż����飨����STP��
            // ������Ϊ�����ó���Ӧ����
            // �˴�����ģ�����ʼ��ġ�
            if (rst.length() <= 0) {
                continue;
            }
            // �����ʼ�
            Map<String, String> realDataMap = new HashMap<String, String>();
            realDataMap.put(SatApiConstant.MAIL_CC, PropertyReader.getProperty("mail.cc"));
            realDataMap.put(SatApiConstant.MAIL_TO, PropertyReader.getProperty("mail.to"));
            realDataMap.put(SatApiConstant.MAIL_CONTENT, rst);
            realDataMap.put(SatApiConstant.MAIL_FROM, PropertyReader.getProperty("mail.from"));
            realDataMap.put(SatApiConstant.MAIL_IS_HTML, "false");
            dataList.add(realDataMap);
        }
        return dataList;
    }

    /**
     * ��Ϣ�����̡� 
     * @return �������Ϣ
     */
    private String contentFiller(List<SystemAlarmConfig> sysAlarmCollection) {
        StringBuilder sb = new StringBuilder();
        for (SystemAlarmConfig config : sysAlarmCollection) {
            String tmp = checkDataForFlush(config.getList(), config.getBatchSize(), config.getContent());
            if (tmp == null) {
                continue;
            }
            sb.append(tmp);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    /**
     * �����Ƿ�����Ҫ���͵ľ�����Ϣ��
     */
    private String checkDataForFlush(List<DeimosSatelliteRequest> alarmList, int limit, String message) {
        if (alarmList.size() < limit) {
            return null;
        }
        int totalNum = alarmList.size();
        // �����������
        TreeSet<Long> timeSets = new TreeSet<Long>();
        Iterator<DeimosSatelliteRequest> iterator = alarmList.iterator();
        while (iterator.hasNext()) {
            timeSets.add(iterator.next().getTimestamp());
        }
        // 5min�ڱĳ���ô����Ǿͷ�������,����������
        return message + "���д��� " + totalNum + "��";
    }

    /**
     * ���ϵͳ������ʼ������
     * @return ��ʼ����ı�������
     */
    private List<SystemAlarmConfig> fillInitData() {
        List<SystemAlarmConfig> sysAlarmCollection = new ArrayList<SystemAlarmConfig>();
        for (AlarmCode meta : AlarmCode.values()) {
            sysAlarmCollection.add(new SystemAlarmConfig(meta.infotype, meta.limit, meta.content));
        }
        return sysAlarmCollection;
    }
}
