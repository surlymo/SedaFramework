package com.baidu.deimos.satellite.bo;

import java.util.ArrayList;
import java.util.List;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.api.constant.Infotype;

/**
 * ϵͳ����bo
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
     * ���캯����ά��һ���յĶ���
     * @param infotype
     *      ��Ϣ����
     * @param batchSize
     *      ������ֵ
     * @param content
     *      ��Ϣ����
     */
    public SystemAlarmConfig(Infotype infotype, Integer batchSize, String content) {
        this.infotype = infotype;
        this.batchSize = batchSize;
        this.content = content;
        this.list = new ArrayList<DeimosSatelliteRequest>();
    }

    /**
     * ���캯���������¹�����У��������������
     * @param infotype
     *      ��Ϣ����
     * @param batchSize
     *      ������ֵ
     * @param content
     *      ��Ϣ����
     * @param list
     *      �����������
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
