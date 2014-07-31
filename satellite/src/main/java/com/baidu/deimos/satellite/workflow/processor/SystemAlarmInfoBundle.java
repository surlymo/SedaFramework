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
 * @description 报警信息分拣。针对error log次数，抛异常次数，线程池请求被拒绝次数，STP交互错误次数进行校验
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Jan 15, 2014 2:35:32 PM
 * @version 1.0
 */
public class SystemAlarmInfoBundle extends SatAmqpBundle {

    @SuppressWarnings({ "unchecked" })
    @Override
    public Object doWork(List<DeimosSatelliteRequest> msgList) throws SatAmqpException {
        // 基础校验，如果为空或null则直接返回，下游函数会将null返回排除。
        if (msgList == null || msgList.isEmpty()) {
            return null;
        }
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        for (DeimosSatelliteRequest metaMsg : msgList) {
            // 系统错误合集
            List<SystemAlarmConfig> sysAlarmCollection = fillInitData();
            // 开始分拣
            for (DeimosSatelliteRequest meta : (List<DeimosSatelliteRequest>) metaMsg.getRealData()) {
                // 遍历调用对应的队列进行填充
                for (SystemAlarmConfig config : sysAlarmCollection) {
                    // 都记录进总错误次数里
                    if (Infotype.INFOTYPE_ALARM_ALL.equals(config.getInfotype())) {
                        config.getList().add(meta);
                        continue;// 防止重复填
                    }
                    if (config.getInfotype().equals(meta.getInfoType())) {
                        config.getList().add(meta);
                    }
                }
            }
            // 往cache里面写当前被应用的次数，比较是否超过每日推送阈值。
            // 此处mock掉认为可以推送
            try {
                Thread.sleep(30);
            } catch (Exception e) {
                throw new RuntimeException("mock biz costing time error!");
            }
            // 进行长度判断。对长度超长的所有队列，取出对应报警信息，
            // 进行邮件和短信推送，一天上限十次
            String rst = contentFiller(sysAlarmCollection);
            // 此处是系统警报，不用推送给客户，所以没有更多的DB交互。
            // 后续如要推送给客户，如通过订阅的方式，
            // 给用户主动推送优化建议（接入STP）
            // 或者行为分析得出对应建议
            // 此处就先模拟推邮件的。
            if (rst.length() <= 0) {
                continue;
            }
            // 发送邮件
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
     * 消息填充过程。 
     * @return 填充后的消息
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
     * 检验是否有需要推送的警报信息。
     */
    private String checkDataForFlush(List<DeimosSatelliteRequest> alarmList, int limit, String message) {
        if (alarmList.size() < limit) {
            return null;
        }
        int totalNum = alarmList.size();
        // 把内容清除掉
        TreeSet<Long> timeSets = new TreeSet<Long>();
        Iterator<DeimosSatelliteRequest> iterator = alarmList.iterator();
        while (iterator.hasNext()) {
            timeSets.add(iterator.next().getTimestamp());
        }
        // 5min内蹦出这么多错，那就发警报吧,最后进行推送
        return message + "共有错误： " + totalNum + "条";
    }

    /**
     * 填充系统警报初始化数据
     * @return 初始化后的报警配置
     */
    private List<SystemAlarmConfig> fillInitData() {
        List<SystemAlarmConfig> sysAlarmCollection = new ArrayList<SystemAlarmConfig>();
        for (AlarmCode meta : AlarmCode.values()) {
            sysAlarmCollection.add(new SystemAlarmConfig(meta.infotype, meta.limit, meta.content));
        }
        return sysAlarmCollection;
    }
}
