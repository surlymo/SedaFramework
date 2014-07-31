/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.core;

import org.apache.log4j.Logger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.baidu.deimos.satellite.constant.DecisionVariety;
import com.baidu.deimos.satellite.core.decision.SatBundleStyleDecision;
import com.baidu.deimos.satellite.core.decision.SatBundleStyleDecisionFactory;
import com.baidu.deimos.satellite.exception.SatAmqpException;

/**
 * @title SatVirtualBundle
 * @description SEDA�Ὣһ�������зֳ������׶Σ�ÿ���׶��䱸��Ӧ���¼����С��̳߳أ����ж�Ӧ���¼�ִ������
 *              SatVirtualBundle����������ÿһ���׶��е��¼�������߱��̳߳ء���ʱ�����������У�
 *              �Ը���ʵ�ֶ�ÿһ���׶��¼����첽����������Ϊ��ܺ��������
 *              Ŀǰ���ڸ���ʵ�ֵ��¼�ִ������collector���ռ�������processor�����������Լ�pusher������������
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public abstract class SatVirtualBundle implements InitializingBean {
    private static final Logger LOGGER = Logger.getLogger(SatVirtualBundle.class);
    /**
     * ���߳�֧�֡�Ĭ��Ϊͬ��ִ��
     */
    private TaskExecutor taskExecutor = new SyncTaskExecutor();
    /**
     * ��ʱ����� ���� ָ��bundle��ʱ������ʱ�䣬����quartz���﷨
     */
    private String timer = "";
    /**
     * ��ʱ����� ���� ����������
     */
    @Autowired
    protected StdSchedulerFactory schedulerFactory;
    /**
     * ��ʱ����� ���� �������������
     */
    protected final String scheduleGroupName = "deimos-group";
    /**
     * bundle�Ƿ񱻼���
     */
    private boolean active = true;
    /**
     * bundle�����������͡�����֧�����û���
     */
    private DecisionVariety strategyDecider;
    /**
     * bundle�������������Խ���bundle��״̬�������Լ���Դ��ء�
     */
    protected SatBundleStyleDecision strategyDecision;

    /**
     * �����Ĺ���������
     * 
     * @throws SatAmqpException
     *      ͨ��amqp�����쳣
     */
    public abstract void work() throws SatAmqpException;

    /**
     * ������飨���Կ�������bundleʹ��һ�����ɣ� 
     */
    public abstract void healthyCheck();

    /**
     * ��bundle����֮ǰ��һЩ���� 
     */
    public abstract void beforeBundleRun();

    /**
     * ��bundle����֮����һЩ���� 
     */
    public abstract void afterBundleRun();

    /**
     * ��Ҫ����������
     */
    @Override
    public void afterPropertiesSet() {
        LOGGER.info("[satellite]bundle: " + this.getClass().getName() + " installed. begin to launch......");
        // ��ʼ�����Ծ�����
        if (strategyDecider == null) {
            strategyDecision = SatBundleStyleDecisionFactory.getInstance(this);
        } else {
            strategyDecision = SatBundleStyleDecisionFactory.getInstance(strategyDecider, this);
        }
        // bundle����ǰ����һЩ����
        beforeBundleRun();
        // ����bundle�Ƿ񱻼���
        if (!strategyDecision.isActived()) {
            return;
        }
        // ִ�н������
        new Thread(new Runnable() {
            @Override
            public void run() {
                strategyDecision.healthycheck();
            }
        }, this.getClass().getName()).start();
        // ��ʼִ�������Ĺ���
        try {
            work();
        } catch (SatAmqpException e) {
            LOGGER.error("[satellite]bundle throw exception while doing work", e);
            faultTolerant();
        }
        // bundle��������һЩ����
        afterBundleRun();
    }

    /**
     * �ݴ��Լ�������
     */
    public void faultTolerant() {
        return;
    }

    /**
     * @return the taskExecutor
     */
    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    /**
     * @param taskExecutor the taskExecutor to set
     */
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * @return the timer
     */
    public String getTimer() {
        return timer;
    }

    /**
     * @param timer the timer to set
     */
    public void setTimer(String timer) {
        this.timer = timer;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
