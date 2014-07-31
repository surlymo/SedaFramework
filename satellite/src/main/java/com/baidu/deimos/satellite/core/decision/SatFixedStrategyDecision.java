package com.baidu.deimos.satellite.core.decision;

import org.springframework.core.task.SyncTaskExecutor;

import com.baidu.deimos.satellite.constant.BundleStatus;
import com.baidu.deimos.satellite.constant.SyncType;
import com.baidu.deimos.satellite.constant.TimelinessType;
import com.baidu.deimos.satellite.core.SatVirtualBundle;

/**
 * �̶����Բ�����bundle������
 * 
 * Created on Jan 15, 2014 2:19:32 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SatFixedStrategyDecision implements SatBundleStyleDecision {

    private SatVirtualBundle satVirtualBundle;

    /**
     * �̶����Բ������������캯��
     * @param satVirtualBundle
     *      �������þ�������bundle
     */
    public SatFixedStrategyDecision(SatVirtualBundle satVirtualBundle) {
        this.satVirtualBundle = satVirtualBundle;
    }

    @Override
    public BundleStatus checkBundleStatus() {
        // TODO ��������heart-check��ͨ���������ͳ�����������״̬ʵʱ����
        return BundleStatus.STATUS_OK;
    }

    @Override
    public boolean isActived() {
        return satVirtualBundle.isActive();
    }

    @Override
    public TimelinessType getTimelinesstype() {
        if (satVirtualBundle.getTimer() == null || satVirtualBundle.getTimer().trim().equals("")) {
            return TimelinessType.REALTIME;
        } else {
            return TimelinessType.TIMER;
        }
    }

    @Override
    public SyncType getSyncType() {
        if (satVirtualBundle.getTaskExecutor() instanceof SyncTaskExecutor) {
            return SyncType.SYNC;
        } else {
            return SyncType.ASYNC;
        }
    }

    @Override
    public void healthycheck() {
        return;
    }

}
