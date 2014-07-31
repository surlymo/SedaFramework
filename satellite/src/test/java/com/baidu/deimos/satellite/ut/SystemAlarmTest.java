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
 * @description ���Դ��󱨾��ܷ�����ִ�кͱ��յ�
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ctx-spring-beans.xml" })
public class SystemAlarmTest {
    // ģ�����е�����
    private static final Integer BATCH_SIZE = 600;
    // ģ�����еĵȴ�ʱ��
    private static final Integer WAITING_TIME = 40000;
    @Autowired
    private RabbitTemplate template;

    /**
     * ϵͳ���� ���� 30s���ռ���600������log�������䷢�ʼ�
     */
    @Test
    public void testSystemAlarm() {
        // ����һ������
        DeimosSatelliteRequest request = new DeimosSatelliteRequest();
        request.setTimestamp(System.currentTimeMillis());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(SatApiConstant.EXCEPTION, null);
        data.put(SatApiConstant.INFOTYPE, Infotype.INFOTYPE_ALARM_ERROR);
        request.setData(data);
        request.setRealData("aaa");
        request.setInfoType(Infotype.INFOTYPE_ALARM_ERROR);
        // ��template�������ͷ���API��������ģ������600��������Ϣ
        for (int i = 0; i < BATCH_SIZE; i++) {
            template.convertAndSend("deimos-common", "collect.alarm.error", request);
        }
        // �ȴ�40s���Ա�֤һ��������������������
        // ���͹��̼��ȴ��������쳣�����������յ�ACK��Ӧ������Ϊ��ȷִ�����
        // ��ʱ��ͬʱ���Ĳ��������е����䣬���յ���Ӧ�ı����ʼ���
        try {
            Thread.sleep(WAITING_TIME);
        } catch (Exception e) {
            Assert.fail("throw exception is not allowed while testing this case");
        }
    }
}
