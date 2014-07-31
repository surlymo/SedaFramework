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
 * @description SEDA会将一个工作切分成数个阶段，每个阶段配备对应的事件队列、线程池，并有对应的事件执行器。
 *              SatVirtualBundle即用来进行每一个阶段中的事件处理。其具备线程池、定时触发器、队列，
 *              以辅助实现对每一个阶段事件的异步化处理。该类为框架核心组件。
 *              目前基于该类实现的事件执行器有collector（收集器）、processor（处理器）以及pusher（推送器）。
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public abstract class SatVirtualBundle implements InitializingBean {
    private static final Logger LOGGER = Logger.getLogger(SatVirtualBundle.class);
    /**
     * 多线程支持。默认为同步执行
     */
    private TaskExecutor taskExecutor = new SyncTaskExecutor();
    /**
     * 定时器相关 —— 指定bundle定时启动的时间，采用quartz的语法
     */
    private String timer = "";
    /**
     * 定时器相关 —— 调度器工厂
     */
    @Autowired
    protected StdSchedulerFactory schedulerFactory;
    /**
     * 定时器相关 —— 调度器任务分组
     */
    protected final String scheduleGroupName = "deimos-group";
    /**
     * bundle是否被激活
     */
    private boolean active = true;
    /**
     * bundle风格决策器类型。用以支持配置化。
     */
    private DecisionVariety strategyDecider;
    /**
     * bundle风格决策器。用以进行bundle的状态决定，以及资源监控。
     */
    protected SatBundleStyleDecision strategyDecision;

    /**
     * 真正的工作处理函数
     * 
     * @throws SatAmqpException
     *      通用amqp处理异常
     */
    public abstract void work() throws SatAmqpException;

    /**
     * 软健康检查（可以考虑所有bundle使用一个即可） 
     */
    public abstract void healthyCheck();

    /**
     * 在bundle启动之前做一些工作 
     */
    public abstract void beforeBundleRun();

    /**
     * 在bundle启动之后做一些工作 
     */
    public abstract void afterBundleRun();

    /**
     * 主要的启动方法
     */
    @Override
    public void afterPropertiesSet() {
        LOGGER.info("[satellite]bundle: " + this.getClass().getName() + " installed. begin to launch......");
        // 初始化策略决策器
        if (strategyDecider == null) {
            strategyDecision = SatBundleStyleDecisionFactory.getInstance(this);
        } else {
            strategyDecision = SatBundleStyleDecisionFactory.getInstance(strategyDecider, this);
        }
        // bundle启动前先做一些事情
        beforeBundleRun();
        // 检验bundle是否被激活
        if (!strategyDecision.isActived()) {
            return;
        }
        // 执行健康检查
        new Thread(new Runnable() {
            @Override
            public void run() {
                strategyDecision.healthycheck();
            }
        }, this.getClass().getName()).start();
        // 开始执行真正的工作
        try {
            work();
        } catch (SatAmqpException e) {
            LOGGER.error("[satellite]bundle throw exception while doing work", e);
            faultTolerant();
        }
        // bundle启动后做一些事情
        afterBundleRun();
    }

    /**
     * 容错以及错误处理
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
