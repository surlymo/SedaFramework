/**
 * Baidu.com Inc. Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.triggers.AbstractTrigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @title SatWorkCarrier
 * @description һ�߸���ִ�е�ִ����
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class SatWorkCarrier extends QuartzJobBean {

    /**
     * �ù��캯���ṩ��Quartz��ʼ��Jobʱ����
     */
    public SatWorkCarrier() {

    }

    /**
     * ��ʱ����ʱִ��
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("i am now in..... current time: " + System.currentTimeMillis());
        System.out.println(CtTest.trigger2Rules.get(((AbstractTrigger) context.getTrigger()).getName()));
        //        System.out.println(((AbstractTrigger) context.getTrigger()).getPreviousFireTime());
    }
}
