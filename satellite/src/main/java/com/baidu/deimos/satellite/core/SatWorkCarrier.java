/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.constant.SatConstant;
import com.baidu.deimos.satellite.exception.SatAmqpException;

/**
 * @title SatWorkCarrier
 * @description һ�߸���ִ�е�ִ����
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class SatWorkCarrier extends QuartzJobBean implements Runnable {
    private List<DeimosSatelliteRequest> msgList = null;
    private SatAmqpBundle bundle = null;

    /**
     * �ù��캯���ṩ��Quartz��ʼ��Jobʱ����
     */
    public SatWorkCarrier() {

    }

    /**
     * ʵʱ����worker��ʱ�����øù��캯������ؼ�����
     * @param msgList
     *       �ռ�������Ϣ
     * @param bundle
     *       ��carrier������bundle
     */
    public SatWorkCarrier(List<DeimosSatelliteRequest> msgList, SatAmqpBundle bundle) {
        this.msgList = msgList;
        this.bundle = bundle;
    }

    /**
     * ����ʵ�ʴ���
     */
    @Override
    public void run() {
        try {
            bundle.pubInfo(bundle.doWork(msgList), bundle.appendExtraMsg());
        } catch (Exception e) {
            throw new SatAmqpException("error occur while processing bundle biz", e);
        }
    }

    /**
     * ��ʱ����ʱִ��
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        ConcurrentLinkedQueue cachedData = (ConcurrentLinkedQueue) dataMap.get(SatConstant.DATA);
        SatAmqpBundle bundle = (SatAmqpBundle) dataMap.get(SatConstant.CARRIER);
        TaskExecutor executor = (TaskExecutor) dataMap.get(SatConstant.EXECUTOR);
        List<DeimosSatelliteRequest> realData = new ArrayList<DeimosSatelliteRequest>();
        // ���δ���ɹ���ʼ��������û���µ����ݽ�������ֱ�ӷ���
        if (cachedData == null || cachedData.isEmpty()) {
            return;
        }
        // ��ʱ���ܰ������µ��������
        while (!cachedData.isEmpty()) {
            Object meta = cachedData.poll();
            if (meta == null) {
                break;
            }
            realData.add((DeimosSatelliteRequest) meta);
        }
        this.bundle = bundle;
        this.msgList = realData;
        // ������ͬ���첽����
        executor.execute(new Runnable() {
            @Override
            public void run() {
                methodAdapter();
            }
        });
    }

    /**
     * �Դ����������������µ�ջ��� 
     */
    private void methodAdapter() {
        this.run();
    }
}
