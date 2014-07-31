/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.workflow.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.api.constant.SatApiConstant;
import com.baidu.deimos.satellite.constant.SatConstant;
import com.baidu.deimos.satellite.core.SatAmqpBundle;
import com.baidu.deimos.satellite.exception.SatAmqpException;

/**
 * @title CommonAmqpCollectBundle
 * @description ����amq��ͨ�������ռ���(Ҫ��Ҫ����ÿ��8��ǿ��pushһ�飿)
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class CommonAmqpCollectBundle extends SatAmqpBundle {
    private static final Logger LOGGER = Logger.getLogger(CommonAmqpCollectBundle.class);
    /**
     * ���ò������С�
     */
    public ConcurrentLinkedQueue<DeimosSatelliteRequest> cacheQueue = new ConcurrentLinkedQueue<DeimosSatelliteRequest>();

    @Override
    public Object doWork(List<DeimosSatelliteRequest> msgList) throws SatAmqpException {
        LOGGER.info("i am now in LogWorker:" + Thread.currentThread().getName() + "message is : " + msgList
                + ". now begin to collect!");
        // ����еȴ��ռ����ﵽ��ֵ
        cacheQueue.addAll(msgList);
        if (cacheQueue.size() < SatConstant.LOG_BATCH_SIZE) {
            return null;
        }
        // ���б���������
        List<DeimosSatelliteRequest> retDataList = new ArrayList<DeimosSatelliteRequest>();
        for (int i = 0; i < SatConstant.LOG_BATCH_SIZE && !cacheQueue.isEmpty(); i++) {
            DeimosSatelliteRequest meta = cacheQueue.poll();
            // ��ʱ����Ҳ�Ѿ�Ϊ����
            if (meta == null) {
                break;
            }
            // У��
            if (meta.getTimestamp() == null || meta.getRealData() == null || meta.getData() == null) {
                LOGGER.error("[deimos-satellite]meta param is error! meta request: " + meta);
                continue;
            }
            // ��¼�ؼ���Ϊ
            if (meta.getData().get(SatApiConstant.KEY_ACTION) == null) {
                meta.getData().put(SatApiConstant.KEY_ACTION, getKeyAction());
            }
            retDataList.add(meta);
        }
        // ���� ����ʱ���Ϊkey�����ǵ����ܳ���ʱ���һ�µ���������Բ�����map��
        // ���ǵ����logҪpush������ƽ̨���߷����ϣ�
        // ����ƬӦ���ȱ�֤���������������ȫ��������һ��bundle������
        Collections.sort(retDataList, new Comparator<DeimosSatelliteRequest>() {
            @Override
            public int compare(DeimosSatelliteRequest o1, DeimosSatelliteRequest o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        LOGGER.info("[deimos-satellite]common amqp collctor prepare to push to next bundle............ key action: "
                + getKeyAction());
        return retDataList;
    }
}
