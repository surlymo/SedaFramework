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
 * @description һ�߸���ִ�е�ִ����
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class CopyOfSatWorkCarrier extends QuartzJobBean {

    /**
     * �ù��캯���ṩ��Quartz��ʼ��Jobʱ����
     */
    public CopyOfSatWorkCarrier() {

    }

    /**
     * ��ʱ����ʱִ��
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("���ڶ�ʱУ��׶�..... current time: " + System.currentTimeMillis());

        try {
            Trigger trigger = CtTest.scheduler.getTrigger(new TriggerKey("autotrigger"));

            System.out.println("������" + ((AbstractTrigger) trigger).getName());
            System.out.println("��һ��ִ��ʱ��" + trigger.getNextFireTime());
            System.out.println("��һ��ִ��ʱ��" + trigger.getPreviousFireTime());
            System.out.println("���ִ��ʱ��" + trigger.getFinalFireTime());
            System.out.println("������ʱ��" + trigger.getStartTime());
            System.out.println("=====================");
            System.out.println(trigger.getFireTimeAfter(new Date(System.currentTimeMillis() - 86400)));
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
