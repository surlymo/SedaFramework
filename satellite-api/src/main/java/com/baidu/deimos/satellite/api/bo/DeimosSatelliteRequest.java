package com.baidu.deimos.satellite.api.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.core.task.SyncTaskExecutor;

import com.baidu.deimos.satellite.api.constant.Infotype;

/**
 * deimos-satellite的通用请求bo，需要实现serializable接口来支持序列化
 * 
 * Created on Jan 15, 2014 2:40:54 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class DeimosSatelliteRequest implements Serializable {
    private static final long serialVersionUID = 2869609553792111230L;
    private String client = "FC-AO";
    private Long timestamp;
    private String pubKey = "collect.log";
    private String destination = "deimos-common";
    private Object realData;
    private Class<?> realDataMetaClazz;
    private Infotype infoType;
    private Map<String, Object> data = new HashMap<String, Object>();

    /**
     * 设置真实数据
     * @param realData
     *      真实的消息数据
     * @author chenchao03
     * @date Jan 14, 2014
     * @returnType void
     */
    @SuppressWarnings("rawtypes")
    public void setRealData(Object realData) {
        
        this.realData = realData;

        // 对集合类型做一下特殊校验
        if (realData != null && realData instanceof Collection) {
            Collection obj = (Collection) realData;
            if (!obj.isEmpty()) {
                this.realDataMetaClazz = obj.toArray()[0].getClass();
            }
        }
    }
      
    /**
     * @return the client
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the pubKey
     */
    public String getPubKey() {
        return pubKey;
    }

    /**
     * @param pubKey the pubKey to set
     */
    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the realDataMetaClazz
     */
    public Class<?> getRealDataMetaClazz() {
        return realDataMetaClazz;
    }

    /**
     * @param realDataMetaClazz the realDataMetaClazz to set
     */
    public void setRealDataMetaClazz(Class<?> realDataMetaClazz) {
        this.realDataMetaClazz = realDataMetaClazz;
    }

    /**
     * @return the infoType
     */
    public Infotype getInfoType() {
        return infoType;
    }

    /**
     * @param infoType the infoType to set
     */
    public void setInfoType(Infotype infoType) {
        this.infoType = infoType;
    }

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * @return the realData
     */
    public Object getRealData() {
        return realData;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

}
