package com.baidu.deimos.satellite.bo;

import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.InitializingBean;

import com.baidu.deimos.satellite.constant.SatConstant;

/**
 * amq���С����������󶨹�ϵ����
 * 
 * Created on Jan 15, 2014 2:21:35 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class AmqpQueueConfig implements InitializingBean {
    private String exchangeName;
    private String queue;
    private String bindingKey;

    /**
     * ת��������������
     */
    private TopicExchange exchange;
    private List<String> bindingKeys;

    /**
     * @return the exchangeName
     */
    public String getExchangeName() {
        return exchangeName;
    }

    /**
     * @param exchangeName the exchangeName to set
     */
    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    /**
     * @return the queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * @param queue the queue to set
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    /**
     * @return the bindingKey
     */
    public String getBindingKey() {
        return bindingKey;
    }

    /**
     * @param bindingKey the bindingKey to set
     */
    public void setBindingKey(String bindingKey) {
        this.bindingKey = bindingKey;
    }

    /**
     * @return the exchange
     */
    public TopicExchange getExchange() {
        return exchange;
    }

    /**
     * @param exchange the exchange to set
     */
    public void setExchange(TopicExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the bindingKeys
     */
    public List<String> getBindingKeys() {
        return bindingKeys;
    }

    /**
     * @param bindingKeys the bindingKeys to set
     */
    public void setBindingKeys(List<String> bindingKeys) {
        this.bindingKeys = bindingKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // ������л��߽�����û�ж��壬��ô�Ͳ���Ĭ��ֵ��ʼ����
        // �����Ϳ��Ա���mq�ĵײ��ʼ����ʹ���ߵ�͸��
        if (exchangeName == null || exchangeName.trim().equals("")) {
            exchangeName = SatConstant.DEFAULT_EXCHANGE;
        }

        if (queue == null || queue.trim().equals("")) {
            // ������������ɵĶ��У�����һ������崻����ٻָ���ʱ�򣬾��Ҳ��������Ķ����ˡ�
            // queue = exchangeName + System.nanoTime();
            queue = bindingKey + "-autoqueue";
        }

        bindingKeys = Arrays.asList(bindingKey.split(","));
        // ��Ҫ�־û�����queue����һ�£���Ȼ�ᱨ��
        exchange = new TopicExchange(exchangeName, true, false);
    }

}
