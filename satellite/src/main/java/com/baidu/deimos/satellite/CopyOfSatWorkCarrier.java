/**
 * Baidu.com Inc. Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.AbstractTrigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @title SatWorkCarrier
 * @description 一线负责执行的执行者
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class CopyOfSatWorkCarrier extends QuartzJobBean {

    /**
     * 该构造函数提供给Quartz初始化Job时候用
     */
    public CopyOfSatWorkCarrier() {

    }

    /**
     * 定时器定时执行
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("我在定时校验阶段..... current time: " + System.currentTimeMillis());

        try {
            Trigger trigger = CtTest.scheduler.getTrigger(new TriggerKey("autotrigger"));

            System.out.println("任务名" + ((AbstractTrigger) trigger).getName());
            System.out.println("下一个执行时间" + trigger.getNextFireTime());
            System.out.println("上一个执行时间" + trigger.getPreviousFireTime());
            System.out.println("最后被执行时间" + trigger.getFinalFireTime());
            System.out.println("被启动时间" + trigger.getStartTime());
            System.out.println("=====================");
            System.out.println(trigger.getFireTimeAfter(new Date(System.currentTimeMillis() - 86400)));
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
