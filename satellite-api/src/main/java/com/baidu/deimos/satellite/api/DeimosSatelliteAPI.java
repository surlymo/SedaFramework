package com.baidu.deimos.satellite.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.api.constant.Infotype;
import com.baidu.deimos.satellite.api.constant.SatApiConstant;
import com.rabbitmq.client.Channel;

/**
 * �ṩ��ʹ�÷���API�����Ը���������Ϣ���͡�
 * 
 * Created on Jan 15, 2014 2:41:25 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class DeimosSatelliteAPI {

    @Autowired
    private RabbitTemplate amqpTemplate;

    boolean keepAlive = false;

    /**
     * ��Ϣ���ͺ�����������Ϣ�������ݼ���
     * 
     * @param content
     *      ��Ϣ����
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(Object content) {
        pub(null, null, null, null, content, null, null, null);
    }

    /**
     * ��Ϣ���ͺ�����������Ϣ�����Լ���Ϣ����
     * 
     * @param content
     *     ��Ϣ��������
     * @param infotype
     *      ��Ϣ����
     * @author chenchao03
     * @date Dec 25, 2013
     */
    public void pub(Object content, Infotype infotype) {
        pub(null, null, null, null, content, null, infotype, null);
    }

    /**
     * ��Ϣ���ͺ��������ͱ���ʹ��
     * 
     * @param content
     *      ��Ϣ����
     * @param infotype
     *      ��Ϣ����
     * @author chenchao03
     * @date Dec 25, 2013
     */
    public void pubAlarm(Object content, Infotype infotype) {
        pub(null, null, "collect.alarm.*", null, content, null, infotype, null);
    }

    /**
     * �������쳣����Ϣ����
     * 
     * @param content
     *      ��Ϣ����
     * @param t
     *      �쳣��Ϣ
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(Object content, Throwable t) {
        pub(null, null, null, null, content, t, null, null);
    }

    /**
     * ����pubkey
     * 
     * @param pubKey
     *      ������Ϣ��key
     * @param content
     *      ��Ϣ����
     * @param t
     *      �쳣��Ϣ
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(String pubKey, Object content, Throwable t) {
        pub(null, null, pubKey, null, content, t, null, null);
    }

    /**
     * ����exchange, �����Ƿ���Ҫ������
     * 
     * @param exchangeName
     *      ����������
     * @param pubKey
     *      ���͵�key
     * @param content
     *      ��Ϣ����
     * @param t
     *      �쳣��Ϣ
     * @author chenchao03
     * @date Nov 30, 2013
     */
    public void pub(String exchangeName, String pubKey, Object content, Throwable t) {
        pub(null, exchangeName, pubKey, null, content, t, null, null);
    }

    /**
     * ����exchange,�����쳣,����keepAlive�͸�����Ϣ
     * 
     * @param exchangeName
     *      ����������
     * @param pubKey
     *      ������Ϣ��key
     * @param content
     *      ��Ϣ����
     * @param keepAlive
     *      �Ƿ񱣳ֳ�����
     * @param appendMsg
     *      ������Ϣ
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(String exchangeName, String pubKey, Object content, boolean keepAlive, 
            Map<String, Object> appendMsg) {
        this.keepAlive = keepAlive;
        pub(null, exchangeName, pubKey, null, content, null, null, appendMsg);
    }

    /**
     * ͨ�÷�����Ϣ����
     * 
     * @param timeStamp
     *      ʱ�����Ĭ��Ϊ��ǰʱ��
     * @param exchangeName
     *      �������Ľ�������Ĭ��Ϊdeimos-common
     * @param pubKey
     *      ����������key��Ĭ��Ϊcollect.log�����ռ�log����Ϊ�����
     * @param client
     *      �ͻ��ˣ�Ĭ��ΪFC-AO
     * @param content
     *      ��Ϣ��������
     * @param e
     *      �쳣��Ϣ��������Ҫprintstack������ֻ�ܰ��������󴫵ݹ�ȥ��
     * @param appendMsg
     *      ���븽����Ϣ
     * @param infoType
     *      ��Ϣ����
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(Long timeStamp, String exchangeName, String pubKey, String client, Object content, Throwable e,
            Infotype infoType, Map<String, Object> appendMsg) {
        if (content == null) {
            return;
        }
        DeimosSatelliteRequest request = new DeimosSatelliteRequest();
        if (client != null && !client.trim().equals("")) {
            request.setClient(client);
        }
        if (exchangeName != null && !exchangeName.trim().equals("")) {
            request.setDestination(exchangeName);
        }
        if (pubKey != null && !pubKey.trim().equals("")) {
            request.setPubKey(pubKey);
        }

        request.setTimestamp(timeStamp == null ? System.currentTimeMillis() : timeStamp);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(SatApiConstant.EXCEPTION, e);
        if (appendMsg != null && !appendMsg.isEmpty()) {
            data.putAll(appendMsg);
        }
        request.setData(data);
        request.setRealData(content);
        request.setInfoType(infoType != null ? infoType : Infotype.INFOTYPE_LOG_INFO);
        amqpTemplate.convertAndSend(request.getDestination(), request.getPubKey(), request);

        // �Ƿ񱣳ֳ����ӣ�Ĭ�ϲ����ֳ����ӣ������Ҫ�����close����
        if (!keepAlive) {
            this.close();
        }
    }

    /**
     * ��logtype����info
     * 
     * @param content
     *      ��Ϣ����
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void infoPub(String content) {
        pub(null, null, "collect.log.info", null, content, null, Infotype.INFOTYPE_LOG_INFO, null);
    }

    /**
     * ��logtype����warn
     * 
     * @param content
     *      ��Ϣ����
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void warnPub(String content) {
        pub(null, null, "collect.log.warn", null, content, null, Infotype.INFOTYPE_LOG_WARN, null);
    }

    /**
     * ��logtype����debug
     * 
     * @param content
     *      ��Ϣ����
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void debugPub(String content) {
        pub(null, null, "collect.log.debug", null, content, null, Infotype.INFOTYPE_LOG_DEBUG, null);
    }

    /**
     * ��logtype����error
     * 
     * @param content
     *      ��Ϣ����
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void errorPub(String content) {
        pub(null, null, "collect.log.error", null, content, null, Infotype.INFOTYPE_MONITOR_INFO, null);
    }

    /**
     * ��logtype����error
     * 
     * @param content
     *      ��Ϣ����
     * @param t
     *      �쳣��Ϣ
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void errorPub(String content, Throwable t) {
        pub(null, null, "collect.log.error", null, content, t, Infotype.INFOTYPE_MONITOR_INFO, null);
    }

    /**
     * ��ʾ�ر�
     * 
     * @author chenchao03
     * @date Nov 30, 2013
     */
    public void close() {
        amqpTemplate.execute(new ChannelCallback<Object>() {
            public Object doInRabbit(Channel channel) throws Exception {
                channel.close();
                channel.abort();
                channel.getConnection().close();
                return null;
            }
        });
    }
}