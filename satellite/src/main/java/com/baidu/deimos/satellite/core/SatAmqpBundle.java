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
 * @description ����AMQP��bundleʵ�֡�SEDA�еĺ���stage�����ʵ��
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
public abstract class SatAmqpBundle extends SatVirtualBundle {
    private static final Logger LOGGER = Logger.getLogger(SatAmqpBundle.class);

    /**
     * �ؼ���Ϊ������process.alarm��ʾһ������������Ϊ���롣
     * ��ʼ��ʱĬ����pubkeyΪkeyaction
     */
    private String keyAction = "";
    /**
     * Ҫ��������Ŀ�ĵأ������跢������Ϣ�������
     */
    private String pubDest = "deimos-common";
    /**
     * ��������Ϣ����Կ���ɷ��������
     * ������ֻ��һ�����ɶ��ķ�������Ҫ���ĵ����ݣ������跢������Ϣ�������
     * ���pubkey�Զ��ŷָ�
     */
    private String pubKeys;
    /**
     * Ҫ���ĵ���Դ
     */
    private String subDest = "deimos-common";
    /**
     * Ҫ���ĵ���Ϣ����Կ���ɽ��ն��
     * ���subkey�Զ��ŷָ�
     */
    private String subKeys;
    /**
     * ��������
     */
    @Autowired
    private RabbitTransactionManager rabbitTransactionManager;
    /**
     * ���ķ�����Ϣ�ķ���
     */
    @Autowired
    private DeimosSatelliteAPI pubAPI;
    /**
     * �ӽ������ļ����ж�����Ϣ
     */
    private Set<TopicExchange> exchanges;
    /**
     * ��mq���м����м�����Ϣ
     */
    private List<AmqpQueueConfig> subQueues;
    /**
     * amqp���ӹ�������
     */
    @Autowired
    private ConnectionFactory cf;
    /**
     * mq���������߲�����
     */
    private Integer concurrency = 10;
    /**
     * ��bundle�����Ͷ���
     */
    private ConcurrentLinkedQueue<Object> pubQueues;
    /**
     * �������ͷ�װ�༯��
     */
    private static final Set<Class<?>> BASIC_TYPES = new HashSet<Class<?>>();
    /**
     * bundle��ʱ����ʱ�����Խ������ݻ���Ķ���
     */
    private ConcurrentLinkedQueue<DeimosSatelliteRequest> bufferQueue = new ConcurrentLinkedQueue<DeimosSatelliteRequest>();

    /**
     * �����Ĺ���������
     */
    @Override
    public void work() {
        LOGGER.info("[satellite]now in amqp bundle's work");
        // ����rabbitmq���������Դ˱�����кͽ��������Զ����ɡ�
        RabbitAdmin admin = new RabbitAdmin(cf);
        // �������кͰ���Ϣ
        Set<String> queueNames = declareQueue(admin);
        // ���ɼ�����
        Object listener = new InnerListener();
        // ��������������
        startupListenContainer(listener, queueNames);
    }

    /**
     * ����������������ʼ����
     * @param listener bundle��Ӧ�ļ�����
     * @param queueNames ���ж�����
     */
    private void startupListenContainer(Object listener, Set<String> queueNames) {
        try {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(cf);
            // ���ü���������
            MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
            container.setMessageListener(adapter);
            // ����Ҫ�����Ķ��� 
            container.setQueueNames(queueNames.toArray(new String[queueNames.size()]));
            // ���ü������еĲ�������
            if (concurrency != null && concurrency.intValue() > 0) {
                container.setConcurrentConsumers(concurrency);
            }
            // ���������������������һ���������ŵ�
            container.setTransactionManager(rabbitTransactionManager);
            // ����Ϊ���������
            container.setChannelTransacted(true);
            // ����Ϊ�Զ�Ӧ��
            container.setAcknowledgeMode(AcknowledgeMode.AUTO);
            // ��������
            container.start();
        } catch (SatAmqpBaseException e) {
            throw new SatAmqpException("error occur while processing the real work", e);
        }
    }

    /**
     * ���������������кͰ󶨣����������ж�����
     * @param admin amq������
     * @return ������Ҫ�����Ķ���������
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
     * @description �ڲ�������
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
     * ͬ���첽���� & ��ʱʵʱ����
     * @param msg �յ�����Ϣ����
     */
    @SuppressWarnings("rawtypes")
    private void fillMsgIntoWorkMethod(DeimosSatelliteRequest msg) throws SatAmqpBaseException {
        // ��msg����У�顣
        if (msg == null) {
            return;
        }
        // ����ת��
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
        // ���û�����ù���ʱ������ֱ�ӿ�ʼ�ռ�
        switch (strategyDecision.getTimelinesstype()) {
        case REALTIME:
            getTaskExecutor().execute(new SatWorkCarrier(Arrays.asList(msg), this));
            break;
        case TIMER:
            // ������еȴ���ʱ������
            bufferQueue.add(msg);
            break;
        default:
            break;
        }
    }

    /**
     * ������飬�ɱ���д��
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
     * ���exchange��queue��keyaction�����������������ͼ��ϵĳ�ʼ��
     */
    @Override
    public void beforeBundleRun() {

        // ��ʼ���������ͷ�װ�༯��
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(Byte.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(Short.class);
        BASIC_TYPES.add(Character.class);
        BASIC_TYPES.add(Float.class);
        BASIC_TYPES.add(Double.class);

        // �����������Ϣ���е���������exchangeĬ�ϲ���deimos-common
        // ����Ĭ��ÿ��subkey������autoqueue�ĺ�׺
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
        // keyAction�趨Ϊ������key��
        // �Դ�������һ��bundle�ռ�����Ϣʱ���Ը�������������Ӧ����
        if (!"".equals(keyAction.trim())) {
            return;
        }
        keyAction = this.getPubKeys();
        // ��ʼ��������
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
     * ���ɶ�ʱ����
     * @param req ��bundle�ռ�����ڼ䣬��Ϣ�����浽�Ķ���
     * @return ���ض�ʱ����ִ����
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
     * ���ɶ�ʱ���Ĵ�����
     * @return ���ش�����
     * @throws ParseException ����quartz�﷨����
     */
    private Trigger createTrigger() throws ParseException {
        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName(subKeys + "-autotrigger");
        trigger.setStartTime(new Date());
        trigger.setCronExpression(new CronExpression(getTimer()));
        return trigger;
    }

    /**
     * ��ͨģʽ�� ����ʵ�ʹ�������
     * ��timer��ʱ�����͵�bundle�У� ����Ҫ������ͨ��timer���������һ����
     * �����doWork�������ٵ��ôκ���
     * @param message ÿ�δ�������Ӧ�����е���Ϣ
     * @return ���ش���֮������͸���һ��bundle������
     * @throws SatAmqpException satellite��amqpʵ��worker�е�ͨ���쳣
     */
    public abstract Object doWork(List<DeimosSatelliteRequest> message) throws SatAmqpException;

    /**
     * ��Ӹ�����Ϣ������д�ļ�������д���ĸ��ļ������ʼ��������շ��˵���Ϣ��
     * @return �������ɵĴ���ӵ�����
     * @throws SatAmqpException δ�����쳣
     */
    public Map<String, Object> appendExtraMsg() throws SatAmqpException {
        return null;
    }

    /**
     * ������Ϣ����һ��Ŀ�ĵ�
     * @param msg ������󣬴���������Ϣ
     * @param appendMsg bundle������ӵ�һЩ��ǩ
     */
    public void pubInfo(Object msg, Map<String, Object> appendMsg) {
        if (msg != null && pubDest != null && pubKeys != null) {
            pubAPI.pub(pubDest, pubKeys, msg, true, appendMsg);
        }
    }

    /**
     * ����ת�������ⱻfastjsonת�������Ͷ�Ӧ����
     * @param output ��ʽ��֮������
     * @param req ����ʽ������Ϣ����ṹ����
     * @return ��ʽ���ɹ����
     */
    @SuppressWarnings({ "rawtypes" })
    protected boolean formatInput(Collection output, DeimosSatelliteRequest req) {
        // ��ͼת��ͨ������
        return formatInput(output, (Collection) req.getRealData(), req.getRealDataMetaClazz());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected boolean formatInput(Collection output, Collection input, Class clazz) {
        // ��������ǿջ��߶��ǻ������ͷ�װ�࣬��ô��ֱ������
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
