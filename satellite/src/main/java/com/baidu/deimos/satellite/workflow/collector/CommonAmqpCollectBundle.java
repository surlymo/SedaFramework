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
 * @description 基于amq的通用任务收集器(要不要考虑每晚8点强制push一遍？)
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public class CommonAmqpCollectBundle extends SatAmqpBundle {
    private static final Logger LOGGER = Logger.getLogger(CommonAmqpCollectBundle.class);
    /**
     * 采用并发队列。
     */
    public ConcurrentLinkedQueue<DeimosSatelliteRequest> cacheQueue = new ConcurrentLinkedQueue<DeimosSatelliteRequest>();

    @Override
    public Object doWork(List<DeimosSatelliteRequest> msgList) throws SatAmqpException {
        LOGGER.info("i am now in LogWorker:" + Thread.currentThread().getName() + "message is : " + msgList
                + ". now begin to collect!");
        // 入队列等待收集数达到阈值
        cacheQueue.addAll(msgList);
        if (cacheQueue.size() < SatConstant.LOG_BATCH_SIZE) {
            return null;
        }
        // 进行遍历导数据
        List<DeimosSatelliteRequest> retDataList = new ArrayList<DeimosSatelliteRequest>();
        for (int i = 0; i < SatConstant.LOG_BATCH_SIZE && !cacheQueue.isEmpty(); i++) {
            DeimosSatelliteRequest meta = cacheQueue.poll();
            // 此时队列也已经为空了
            if (meta == null) {
                break;
            }
            // 校验
            if (meta.getTimestamp() == null || meta.getRealData() == null || meta.getData() == null) {
                LOGGER.error("[deimos-satellite]meta param is error! meta request: " + meta);
                continue;
            }
            // 记录关键行为
            if (meta.getData().get(SatApiConstant.KEY_ACTION) == null) {
                meta.getData().put(SatApiConstant.KEY_ACTION, getKeyAction());
            }
            retDataList.add(meta);
        }
        // 排序 ，以时间戳为key。考虑到可能出现时间戳一致的情况，所以不能用map。
        // 考虑到如果log要push到其他平台或者服务上，
        // 该切片应该先保证自身有序而不能完全依赖于下一个bundle来处理
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
