package com.baidu.deimos.satellite.bo;

import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.InitializingBean;

import com.baidu.deimos.satellite.constant.SatConstant;

/**
 * amq队列、交换机、绑定关系配置
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
     * 转换成真正的类型
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

        // 如果队列或者交换机没有定义，那么就采用默认值初始化。
        // 这样就可以保持mq的底层初始化对使用者的透明
        if (exchangeName == null || exchangeName.trim().equals("")) {
            exchangeName = SatConstant.DEFAULT_EXCHANGE;
        }

        if (queue == null || queue.trim().equals("")) {
            // 不采用随机生成的队列，这样一旦碰到宕机，再恢复的时候，就找不到本来的队列了。
            // queue = exchangeName + System.nanoTime();
            queue = bindingKey + "-autoqueue";
        }

        bindingKeys = Arrays.asList(bindingKey.split(","));
        // 需要持久化，和queue保持一致，不然会报错。
        exchange = new TopicExchange(exchangeName, true, false);
    }

}
