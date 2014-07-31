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
 * @description 一线负责执行的执行者
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class SatWorkCarrier extends QuartzJobBean implements Runnable {
    private List<DeimosSatelliteRequest> msgList = null;
    private SatAmqpBundle bundle = null;

    /**
     * 该构造函数提供给Quartz初始化Job时候用
     */
    public SatWorkCarrier() {

    }

    /**
     * 实时调用worker的时候，引用该构造函数传入关键数据
     * @param msgList
     *       收集到的消息
     * @param bundle
     *       该carrier的宿主bundle
     */
    public SatWorkCarrier(List<DeimosSatelliteRequest> msgList, SatAmqpBundle bundle) {
        this.msgList = msgList;
        this.bundle = bundle;
    }

    /**
     * 进行实际处理
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
     * 定时器定时执行
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        ConcurrentLinkedQueue cachedData = (ConcurrentLinkedQueue) dataMap.get(SatConstant.DATA);
        SatAmqpBundle bundle = (SatAmqpBundle) dataMap.get(SatConstant.CARRIER);
        TaskExecutor executor = (TaskExecutor) dataMap.get(SatConstant.EXECUTOR);
        List<DeimosSatelliteRequest> realData = new ArrayList<DeimosSatelliteRequest>();
        // 如果未被成功初始化，或者没有新的数据进来，则直接返回
        if (cachedData == null || cachedData.isEmpty()) {
            return;
        }
        // 此时可能伴随着新的请求进来
        while (!cachedData.isEmpty()) {
            Object meta = cachedData.poll();
            if (meta == null) {
                break;
            }
            realData.add((DeimosSatelliteRequest) meta);
        }
        this.bundle = bundle;
        this.msgList = realData;
        // 继续受同步异步控制
        executor.execute(new Runnable() {
            @Override
            public void run() {
                methodAdapter();
            }
        });
    }

    /**
     * 以此来避免重名而导致的栈溢出 
     */
    private void methodAdapter() {
        this.run();
    }
}
