package com.baidu.deimos.satellite;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronExpression;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

public class CtTest {
    public static Map<String, List<Long>> trigger2Rules = new HashMap<String, List<Long>>();
    public static List<CronTriggerImpl> triggerImpls = new ArrayList<CronTriggerImpl>();
    public static Scheduler scheduler = null;

    public static void main(String[] args) throws SchedulerException, ParseException, InterruptedException {
        triggerTimer();
        //        create();
        //        //        Thread.sleep(1500);
        //        //        del();
        //        createCheckerTimer();
        //        triggerValidator();
    }

    public static void del() throws SchedulerException, ParseException {
        JobKey key = new JobKey("jobdetail");
        scheduler.deleteJob(key);
    }

    public static void create() throws SchedulerException, ParseException {
        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName("autotrigger");
        //        trigger.setCalendarName("autotrigger");
        trigger.setStartTime(new Date());
        trigger.setCronExpression(new CronExpression("0/2 * * * * ?"));

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName("jobdetail");
        jobDetail.setJobClass(SatWorkCarrier.class);
        //        jobDetail.setGroup("aotest");

        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.scheduleJob(jobDetail, trigger);
        triggerImpls.add(trigger);
        scheduler.start();

        List<Long> rules = new ArrayList<Long>();
        rules.add(10001L);
        trigger2Rules.put(trigger.getName(), rules);
    }

    public static void createCheckerTimer() throws SchedulerException, ParseException, InterruptedException {
        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName("autotrigger2");
        trigger.setStartTime(new Date());
        trigger.setCronExpression(new CronExpression("0/10 * * * * ?"));

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName("jobdetail2");
        jobDetail.setJobClass(CopyOfSatWorkCarrier.class);

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public static void triggerTimer() throws SchedulerException, ParseException, InterruptedException {

        //        Calendar calendar = Calendar.getInstance();
        //        System.out.println(calendar.getTime());
        //        calendar.add(Calendar.DAY_OF_YEAR, -1);
        //        System.out.println(calendar.getTime());

        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName("autotrigger2");
        Calendar calendar = Calendar.getInstance();

        trigger.setStartTime(calendar.getTime());
        trigger.setCronExpression(new CronExpression("0/2 * * * * ?"));
        System.out.println(calendar.getTime());
        //        calendar.add(Calendar.SECOND, -1);
        //        System.out.println(calendar.getTime());
        //        System.out.println(trigger.getFireTimeAfter(calendar.getTime()));
        System.out.println(trigger.willFireOn(calendar));
    }

    public static void triggerValidator() throws SchedulerException, ParseException, InterruptedException {

        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        System.out.println(calendar.getTime());

        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName("autotrigger2");
        trigger.setStartTime(calendar.getTime());
        trigger.setCronExpression(new CronExpression("*"));
        System.out.println(trigger.getFireTimeAfter(calendar.getTime()));

    }
}
