/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.core;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.deimos.satellite.api.DeimosSatelliteAPI;
import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.bo.AmqpQueueConfig;
import com.baidu.deimos.satellite.constant.SatConstant;
import com.baidu.deimos.satellite.constant.TimelinessType;
import com.baidu.deimos.satellite.exception.SatAmqpBaseException;
import com.baidu.deimos.satellite.exception.SatAmqpException;

/**
 * @title SatAmqpBundle
 * @description 基于AMQP的bundle实现。SEDA中的核心stage组件的实现
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public abstract class SatAmqpBundle extends SatVirtualBundle {
    private static final Logger LOGGER = Logger.getLogger(SatAmqpBundle.class);

    /**
     * 关键行为。比如process.alarm表示一个处理报警的行为申请。
     * 初始化时默认以pubkey为keyaction
     */
    private String keyAction = "";
    /**
     * 要发布到的目的地（如无需发布到消息队列则不填）
     */
    private String pubDest = "deimos-common";
    /**
     * 发布的消息的密钥，可发布多个。
     * 但建议只发一个，由订阅方来决定要订阅的内容（如无需发布到消息队列则不填）
     * 多个pubkey以逗号分隔
     */
    private String pubKeys;
    /**
     * 要订阅的来源
     */
    private String subDest = "deimos-common";
    /**
     * 要订阅的消息的密钥，可接收多个
     * 多个subkey以逗号分隔
     */
    private String subKeys;
    /**
     * 控制事务
     */
    @Autowired
    private RabbitTransactionManager rabbitTransactionManager;
    /**
     * 订阅发布消息的服务
     */
    @Autowired
    private DeimosSatelliteAPI pubAPI;
    /**
     * 从交换机的集合中订阅消息
     */
    private Set<TopicExchange> exchanges;
    /**
     * 从mq队列集合中监听消息
     */
    private List<AmqpQueueConfig> subQueues;
    /**
     * amqp连接工厂配置
     */
    @Autowired
    private ConnectionFactory cf;
    /**
     * mq队列消费者并发量
     */
    private Integer concurrency = 10;
    /**
     * 该bundle的推送队列
     */
    private ConcurrentLinkedQueue<Object> pubQueues;
    /**
     * 基础类型封装类集合
     */
    private static final Set<Class<?>> BASIC_TYPES = new HashSet<Class<?>>();
    /**
     * bundle定时启动时候用以进行数据缓存的队列
     */
    private ConcurrentLinkedQueue<DeimosSatelliteRequest> bufferQueue = new ConcurrentLinkedQueue<DeimosSatelliteRequest>();

    /**
     * 真正的工作处理函数
     */
    @Override
    public void work() {
        LOGGER.info("[satellite]now in amqp bundle's work");
        // 声明rabbitmq管理器。以此避免队列和交换机被自动生成。
        RabbitAdmin admin = new RabbitAdmin(cf);
        // 声明队列和绑定信息
        Set<String> queueNames = declareQueue(admin);
        // 生成监听器
        Object listener = new InnerListener();
        // 启动监听容器。
        startupListenContainer(listener, queueNames);
    }

    /**
     * 启动监听容器，开始监听
     * @param listener bundle对应的监听器
     * @param queueNames 所有队列名
     */
    private void startupListenContainer(Object listener, Set<String> queueNames) {
        try {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(cf);
            // 设置监听处理器
            MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
            container.setMessageListener(adapter);
            // 设置要监听的队列 
            container.setQueueNames(queueNames.toArray(new String[queueNames.size()]));
            // 设置监听队列的并发数。
            if (concurrency != null && concurrency.intValue() > 0) {
                container.setConcurrentConsumers(concurrency);
            }
            // 设置事务管理器，将开启一个单独的信道
            container.setTransactionManager(rabbitTransactionManager);
            // 设置为收事务管理
            container.setChannelTransacted(true);
            // 设置为自动应答
            container.setAcknowledgeMode(AcknowledgeMode.AUTO);
            // 启动监听
            container.start();
        } catch (SatAmqpBaseException e) {
            throw new SatAmqpException("error occur while processing the real work", e);
        }
    }

    /**
     * 声明交换机、队列和绑定，并返回所有队列名
     * @param admin amq管理器
     * @return 返回需要声明的队列名集合
     */
    private Set<String> declareQueue(RabbitAdmin admin) {
        Set<String> queueNames = new HashSet<String>();
        try {
            for (TopicExchange exchange : this.getExchanges()) {
                admin.declareExchange(exchange);
            }
            for (AmqpQueueConfig queueConfig : this.getSubQueues()) {
                Queue queue = new Queue(queueConfig.getQueue());
                admin.declareQueue(queue);
                queueNames.add(queueConfig.getQueue());
                for (String meta : queueConfig.getBindingKeys()) {
                    admin.declareBinding(BindingBuilder.bind(queue).to(queueConfig.getExchange()).with(meta));
                }
            }
        } catch (SatAmqpBaseException e) {
            throw new SatAmqpException("error occur while processing the real work", e);
        }
        return queueNames;
    }

    /**
     * @description 内部监听器
     * @date Created on Mar 20, 2014 8:01:59 PM
     * @author Chao Chen(chenchao03@baidu.com)
     * @version 1.0
     * @since 1.0
     */
    @SuppressWarnings("unused")
    private class InnerListener {
        public void handleMessage(String message) throws SatAmqpBaseException {
            fillMsgIntoWorkMethod(JSON.parseObject(message, DeimosSatelliteRequest.class));
        }

        public void handleMessage(byte[] message) throws SatAmqpBaseException {
            fillMsgIntoWorkMethod(JSON.parseObject(new String(message), DeimosSatelliteRequest.class));
        }

        public void handleMessage(DeimosSatelliteRequest message) throws SatAmqpBaseException {
            fillMsgIntoWorkMethod(message);
        }
    }

    /**
     * 同步异步控制 & 定时实时控制
     * @param msg 收到的消息请求
     */
    @SuppressWarnings("rawtypes")
    private void fillMsgIntoWorkMethod(DeimosSatelliteRequest msg) throws SatAmqpBaseException {
        // 对msg进行校验。
        if (msg == null) {
            return;
        }
        // 类型转换
        Object realData = msg.getRealData();
        if (realData != null && realData instanceof Collection) {
            Collection output = null;
            if (realData instanceof List) {
                output = new ArrayList();
            } else if (realData instanceof Set) {
                output = new HashSet();
            } else {
                return;
            }
            if (formatInput(output, msg)) {
                msg.setRealData(output);
            }
        }
        // 如果没有设置过定时器，则直接开始收集
        switch (strategyDecision.getTimelinesstype()) {
        case REALTIME:
            getTaskExecutor().execute(new SatWorkCarrier(Arrays.asList(msg), this));
            break;
        case TIMER:
            // 放入队列等待定时器触发
            bufferQueue.add(msg);
            break;
        default:
            break;
        }
    }

    /**
     * 软健康检查，可被重写。
     */
    public void healthyCheck() {
        LOGGER.info("[satellite]now in collector bundle's healthy-check");
        return;
    }

    @Override
    public void afterBundleRun() {
        LOGGER.info("[satellite]now finish bundle running.");
    }

    /**
     * 完成exchange、queue、keyaction、调度器、基础类型集合的初始化
     */
    @Override
    public void beforeBundleRun() {

        // 初始化基础类型封装类集合
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(Byte.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(Short.class);
        BASIC_TYPES.add(Character.class);
        BASIC_TYPES.add(Float.class);
        BASIC_TYPES.add(Double.class);

        // 如果不存在消息队列的声明，则exchange默认采用deimos-common
        // 队列默认每个subkey名加上autoqueue的后缀
        exchanges = new HashSet<TopicExchange>();
        if (subQueues == null || subQueues.isEmpty()) {
            AmqpQueueConfig config = new AmqpQueueConfig();
            config.setQueue(subKeys + "-autoqueue");
            config.setBindingKey(subKeys);
            config.setBindingKeys(Arrays.asList(subKeys.split(",")));
            config.setExchange(new TopicExchange(subDest, true, false));
            subQueues = Arrays.asList(config);
        }
        for (AmqpQueueConfig meta : subQueues) {
            exchanges.add(meta.getExchange());
        }
        // keyAction设定为发布的key。
        // 以此来再下一个bundle收集到信息时，对该数据能做出对应处理
        if (!"".equals(keyAction.trim())) {
            return;
        }
        keyAction = this.getPubKeys();
        // 初始化调度器
        try {
            if (strategyDecision.getTimelinesstype().equals(TimelinessType.TIMER)) {
                Scheduler scheduler = schedulerFactory.getScheduler();
                JobDetail jobDetail = createJobDetail(bufferQueue);
                Trigger trigger = createTrigger();
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
            }
        } catch (SchedulerException e) {
            throw new SatAmqpBaseException("[deimos-satellite]schedule error while starting", e);
        } catch (ParseException e) {
            throw new SatAmqpBaseException("[deimos-satellite]parse error while starting", e);
        }
    }

    /**
     * 生成定时任务
     * @param req 在bundle收集间隔期间，消息被缓存到的队列
     * @return 返回定时任务执行器
     */
    private JobDetail createJobDetail(ConcurrentLinkedQueue<DeimosSatelliteRequest> req) {
        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName(subKeys + "-autojob");
        jobDetail.setJobClass(SatWorkCarrier.class);
        jobDetail.setGroup(scheduleGroupName);
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put(SatConstant.DATA, req);
        jobDataMap.put(SatConstant.CARRIER, this);
        jobDataMap.put(SatConstant.EXECUTOR, getTaskExecutor());
        return jobDetail;
    }

    /**
     * 生成定时器的触发器
     * @return 返回触发器
     * @throws ParseException 解析quartz语法错误
     */
    private Trigger createTrigger() throws ParseException {
        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName(subKeys + "-autotrigger");
        trigger.setStartTime(new Date());
        trigger.setCronExpression(new CronExpression(getTimer()));
        return trigger;
    }

    /**
     * 普通模式下 进行实际工作处理
     * 在timer计时器类型的bundle中， 当需要将所有通过timer缓冲的数据一次性
     * 输出到doWork函数中再调用次函数
     * @param message 每次处理所对应的所有的消息
     * @return 返回处理之后待推送给下一个bundle的数据
     * @throws SatAmqpException satellite的amqp实现worker中的通用异常
     */
    public abstract Object doWork(List<DeimosSatelliteRequest> message) throws SatAmqpException;

    /**
     * 添加附加信息。比如写文件需声明写入哪个文件，发邮件需声明收发人的信息等
     * @return 返回生成的待添加的数据
     * @throws SatAmqpException 未捕获异常
     */
    public Map<String, Object> appendExtraMsg() throws SatAmqpException {
        return null;
    }

    /**
     * 发布消息到下一个目的地
     * @param msg 被处理后，待发布的消息
     * @param appendMsg bundle额外添加的一些标签
     */
    public void pubInfo(Object msg, Map<String, Object> appendMsg) {
        if (msg != null && pubDest != null && pubKeys != null) {
            pubAPI.pub(pubDest, pubKeys, msg, true, appendMsg);
        }
    }

    /**
     * 类型转换。避免被fastjson转换完类型对应不上
     * @param output 格式化之后的输出
     * @param req 待格式化的消息请求结构数据
     * @return 格式化成功与否
     */
    @SuppressWarnings({ "rawtypes" })
    protected boolean formatInput(Collection output, DeimosSatelliteRequest req) {
        // 试图转成通用请求。
        return formatInput(output, (Collection) req.getRealData(), req.getRealDataMetaClazz());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected boolean formatInput(Collection output, Collection input, Class clazz) {
        // 如果集合是空或者都是基础类型封装类，那么就直接跳过
        if (clazz == null || input.isEmpty() || BASIC_TYPES.contains(input.toArray()[0].getClass())
                || Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        for (Object obj : input) {
            output.add(JSONObject.toJavaObject((JSONObject) obj, clazz));
        }
        return true;
    }

    /**
     * @return the exchanges
     */
    public Set<TopicExchange> getExchanges() {
        return exchanges;
    }

    /**
     * @param exchanges the exchanges to set
     */
    public void setExchanges(Set<TopicExchange> exchanges) {
        this.exchanges = exchanges;
    }

    /**
     * @return the concurrency
     */
    public Integer getConcurrency() {
        return concurrency;
    }

    /**
     * @param concurrency the concurrency to set
     */
    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    /**
     * @return the keyAction
     */
    public String getKeyAction() {
        return keyAction;
    }

    /**
     * @param keyAction the keyAction to set
     */
    public void setKeyAction(String keyAction) {
        this.keyAction = keyAction;
    }

    /**
     * @return the pubDest
     */
    public String getPubDest() {
        return pubDest;
    }

    /**
     * @param pubDest the pubDest to set
     */
    public void setPubDest(String pubDest) {
        this.pubDest = pubDest;
    }

    /**
     * @return the pubKeys
     */
    public String getPubKeys() {
        return pubKeys;
    }

    /**
     * @param pubKeys the pubKeys to set
     */
    public void setPubKeys(String pubKeys) {
        this.pubKeys = pubKeys;
    }

    /**
     * @return the pubQueues
     */
    public ConcurrentLinkedQueue<Object> getPubQueues() {
        return pubQueues;
    }

    /**
     * @param pubQueues the pubQueues to set
     */
    public void setPubQueues(ConcurrentLinkedQueue<Object> pubQueues) {
        this.pubQueues = pubQueues;
    }

    /**
     * @return the bufferQueue
     */
    public ConcurrentLinkedQueue<DeimosSatelliteRequest> getBufferQueue() {
        return bufferQueue;
    }

    /**
     * @param bufferQueue the bufferQueue to set
     */
    public void setBufferQueue(ConcurrentLinkedQueue<DeimosSatelliteRequest> bufferQueue) {
        this.bufferQueue = bufferQueue;
    }

    /**
     * @return the subDest
     */
    public String getSubDest() {
        return subDest;
    }

    /**
     * @param subDest the subDest to set
     */
    public void setSubDest(String subDest) {
        this.subDest = subDest;
    }

    /**
     * @return the subKeys
     */
    public String getSubKeys() {
        return subKeys;
    }

    /**
     * @param subKeys the subKeys to set
     */
    public void setSubKeys(String subKeys) {
        this.subKeys = subKeys;
    }

    /**
     * @return the pubAPI
     */
    public DeimosSatelliteAPI getPubAPI() {
        return pubAPI;
    }

    /**
     * @param pubAPI the pubAPI to set
     */
    public void setPubAPI(DeimosSatelliteAPI pubAPI) {
        this.pubAPI = pubAPI;
    }

    /**
     * @return the subQueues
     */
    public List<AmqpQueueConfig> getSubQueues() {
        return subQueues;
    }

    /**
     * @param subQueues the subQueues to set
     */
    public void setSubQueues(List<AmqpQueueConfig> subQueues) {
        this.subQueues = subQueues;
    }
}
