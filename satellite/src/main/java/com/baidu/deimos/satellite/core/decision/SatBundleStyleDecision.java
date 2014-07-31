package com.baidu.deimos.satellite.core.decision;

import com.baidu.deimos.satellite.constant.BundleStatus;
import com.baidu.deimos.satellite.constant.SyncType;
import com.baidu.deimos.satellite.constant.TimelinessType;

/**
 * bundle�������������ڲ�ͬbundle���Զ��Ʋ�һ���ľ�������
 * �����Խӿ���ʽ�ṩ �˴����н��������ơ��ͼ��bundle���ƣ��������ۺϵ�������д
 * 
 * Created on Jan 15, 2014 2:18:09 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public interface SatBundleStyleDecision {

    /**
     * ����bundle״̬
     * @return
     * @author chenchao03
     * @date Jan 14, 2014
     * @return BundleStatus
     */
    BundleStatus checkBundleStatus();

    /**
     * ��bundle�ļ���״̬������/������
     * @return
     * @author chenchao03
     * @date Jan 14, 2014
     * @return boolean
     */
    boolean isActived();

    /**
     * ��bundle��ʱЧ�����͡�ʵʱ/��ʱ
     * @return
     * @author chenchao03
     * @date Jan 14, 2014
     * @return TimelinessType
     */
    TimelinessType getTimelinesstype();

    /**
     * ��bundle��ͬ�����͡�ͬ��/�첽
     * @return
     *      ����bundle��ͬ������
     * @author chenchao03
     * @date Jan 14, 2014
     * @returnType SyncType
     */
    SyncType getSyncType();

    /**
     * �������
     * 
     * @author chenchao03
     * @date Jan 14, 2014
     * @returnType void
     */
    void healthycheck();
}
