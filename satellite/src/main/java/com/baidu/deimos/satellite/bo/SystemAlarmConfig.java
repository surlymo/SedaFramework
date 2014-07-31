package com.baidu.deimos.satellite.bo;

import java.util.ArrayList;
import java.util.List;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.api.constant.Infotype;

/**
 * 系统报警bo
 * 
 * Created on Jan 15, 2014 2:22:18 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SystemAlarmConfig {
    private Infotype infotype;
    private List<DeimosSatelliteRequest> list;
    private Integer batchSize;
    private String content;

    /**
     * 构造函数。维护一个空的队列
     * @param infotype
     *      消息类型
     * @param batchSize
     *      报警阈值
     * @param content
     *      消息内容
     */
    public SystemAlarmConfig(Infotype infotype, Integer batchSize, String content) {
        this.infotype = infotype;
        this.batchSize = batchSize;
        this.content = content;
        this.list = new ArrayList<DeimosSatelliteRequest>();
    }

    /**
     * 构造函数。不重新构造队列，用现有输入队列
     * @param infotype
     *      消息类型
     * @param batchSize
     *      报警阈值
     * @param content
     *      消息内容
     * @param list
     *      现有输入队列
     */
    public SystemAlarmConfig(Infotype infotype, Integer batchSize, String content, List<DeimosSatelliteRequest> list) {
        this.infotype = infotype;
        this.batchSize = batchSize;
        this.content = content;
        this.list = list;
    }

    /**
     * @return the infotype
     */
    public Infotype getInfotype() {
        return infotype;
    }

    /**
     * @param infotype the infotype to set
     */
    public void setInfotype(Infotype infotype) {
        this.infotype = infotype;
    }

    /**
     * @return the list
     */
    public List<DeimosSatelliteRequest> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<DeimosSatelliteRequest> list) {
        this.list = list;
    }

    /**
     * @return the batchSize
     */
    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * @param batchSize the batchSize to set
     */
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

}
