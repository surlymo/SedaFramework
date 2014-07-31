package com.baidu.deimos.satellite.ut;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.api.constant.Infotype;
import com.baidu.deimos.satellite.api.constant.SatApiConstant;

/**
 * @title SystemAlarmTest
 * @description 测试错误报警能否正常执行和被收到
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ctx-spring-beans.xml" })
public class SystemAlarmTest {
    // 模拟运行的批次
    private static final Integer BATCH_SIZE = 600;
    // 模拟运行的等待时间
    private static final Integer WAITING_TIME = 40000;
    @Autowired
    private RabbitTemplate template;

    /**
     * 系统报警 —— 30s内收集到600条错误log，朝邮箱发邮件
     */
    @Test
    public void testSystemAlarm() {
        // 构造一个请求
        DeimosSatelliteRequest request = new DeimosSatelliteRequest();
        request.setTimestamp(System.currentTimeMillis());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(SatApiConstant.EXCEPTION, null);
        data.put(SatApiConstant.INFOTYPE, Infotype.INFOTYPE_ALARM_ERROR);
        request.setData(data);
        request.setRealData("aaa");
        request.setInfoType(Infotype.INFOTYPE_ALARM_ERROR);
        // 将template赋给推送服务API，并最终模拟推送600条错误信息
        for (int i = 0; i < BATCH_SIZE; i++) {
            template.convertAndSend("deimos-common", "collect.alarm.error", request);
        }
        // 等待40s，以保证一定能启动报警处理器。
        // 发送过程即等待过程无异常，且能正常收到ACK响应，则认为正确执行完毕
        // 此时可同时查阅测试配置中的邮箱，可收到对应的报警邮件。
        try {
            Thread.sleep(WAITING_TIME);
        } catch (Exception e) {
            Assert.fail("throw exception is not allowed while testing this case");
        }
    }
}
