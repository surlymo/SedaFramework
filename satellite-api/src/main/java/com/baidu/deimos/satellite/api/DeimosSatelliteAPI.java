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
 * 提供给使用方的API，用以辅助进行消息推送。
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
     * 消息推送函数。仅需消息主体内容即可
     * 
     * @param content
     *      消息内容
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(Object content) {
        pub(null, null, null, null, content, null, null, null);
    }

    /**
     * 消息推送函数。仅需消息内容以及消息类型
     * 
     * @param content
     *     消息内容主体
     * @param infotype
     *      消息类型
     * @author chenchao03
     * @date Dec 25, 2013
     */
    public void pub(Object content, Infotype infotype) {
        pub(null, null, null, null, content, null, infotype, null);
    }

    /**
     * 消息推送函数。推送报警使用
     * 
     * @param content
     *      消息内容
     * @param infotype
     *      消息类型
     * @author chenchao03
     * @date Dec 25, 2013
     */
    public void pubAlarm(Object content, Infotype infotype) {
        pub(null, null, "collect.alarm.*", null, content, null, infotype, null);
    }

    /**
     * 带错误异常的消息推送
     * 
     * @param content
     *      消息内容
     * @param t
     *      异常信息
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(Object content, Throwable t) {
        pub(null, null, null, null, content, t, null, null);
    }

    /**
     * 带上pubkey
     * 
     * @param pubKey
     *      发布消息的key
     * @param content
     *      消息内容
     * @param t
     *      异常信息
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(String pubKey, Object content, Throwable t) {
        pub(null, null, pubKey, null, content, t, null, null);
    }

    /**
     * 带上exchange, 设置是否需要长连接
     * 
     * @param exchangeName
     *      交换机名字
     * @param pubKey
     *      推送的key
     * @param content
     *      消息内容
     * @param t
     *      异常信息
     * @author chenchao03
     * @date Nov 30, 2013
     */
    public void pub(String exchangeName, String pubKey, Object content, Throwable t) {
        pub(null, exchangeName, pubKey, null, content, t, null, null);
    }

    /**
     * 带上exchange,不带异常,带上keepAlive和附加信息
     * 
     * @param exchangeName
     *      交换机名字
     * @param pubKey
     *      推送消息的key
     * @param content
     *      消息内容
     * @param keepAlive
     *      是否保持长连接
     * @param appendMsg
     *      附加信息
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void pub(String exchangeName, String pubKey, Object content, boolean keepAlive, 
            Map<String, Object> appendMsg) {
        this.keepAlive = keepAlive;
        pub(null, exchangeName, pubKey, null, content, null, null, appendMsg);
    }

    /**
     * 通用发布消息函数
     * 
     * @param timeStamp
     *      时间戳。默认为当前时间
     * @param exchangeName
     *      发布到的交换机。默认为deimos-common
     * @param pubKey
     *      发布的内容key。默认为collect.log，即收集log。因为其最常用
     * @param client
     *      客户端，默认为FC-AO
     * @param content
     *      消息内容主体
     * @param e
     *      异常信息。由于需要printstack，所以只能把整个对象传递过去。
     * @param appendMsg
     *      填入附加信息
     * @param infoType
     *      消息类型
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

        // 是否保持长连接，默认不保持长连接，如果需要则调用close函数
        if (!keepAlive) {
            this.close();
        }
    }

    /**
     * 给logtype打上info
     * 
     * @param content
     *      消息内容
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void infoPub(String content) {
        pub(null, null, "collect.log.info", null, content, null, Infotype.INFOTYPE_LOG_INFO, null);
    }

    /**
     * 给logtype打上warn
     * 
     * @param content
     *      消息内容
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void warnPub(String content) {
        pub(null, null, "collect.log.warn", null, content, null, Infotype.INFOTYPE_LOG_WARN, null);
    }

    /**
     * 给logtype打上debug
     * 
     * @param content
     *      消息内容
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void debugPub(String content) {
        pub(null, null, "collect.log.debug", null, content, null, Infotype.INFOTYPE_LOG_DEBUG, null);
    }

    /**
     * 给logtype打上error
     * 
     * @param content
     *      消息内容
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void errorPub(String content) {
        pub(null, null, "collect.log.error", null, content, null, Infotype.INFOTYPE_MONITOR_INFO, null);
    }

    /**
     * 给logtype打上error
     * 
     * @param content
     *      消息内容
     * @param t
     *      异常信息
     * @author chenchao03
     * @date Nov 29, 2013
     */
    public void errorPub(String content, Throwable t) {
        pub(null, null, "collect.log.error", null, content, t, Infotype.INFOTYPE_MONITOR_INFO, null);
    }

    /**
     * 显示关闭
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