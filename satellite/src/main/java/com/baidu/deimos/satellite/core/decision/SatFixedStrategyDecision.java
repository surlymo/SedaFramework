package com.baidu.deimos.satellite.core.decision;

import org.springframework.core.task.SyncTaskExecutor;

import com.baidu.deimos.satellite.constant.BundleStatus;
import com.baidu.deimos.satellite.constant.SyncType;
import com.baidu.deimos.satellite.constant.TimelinessType;
import com.baidu.deimos.satellite.core.SatVirtualBundle;

/**
 * 固定策略参数的bundle决策器
 * 
 * Created on Jan 15, 2014 2:19:32 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SatFixedStrategyDecision implements SatBundleStyleDecision {

    private SatVirtualBundle satVirtualBundle;

    /**
     * 固定策略参数决策器构造函数
     * @param satVirtualBundle
     *      请求分配该决策器的bundle
     */
    public SatFixedStrategyDecision(SatVirtualBundle satVirtualBundle) {
        this.satVirtualBundle = satVirtualBundle;
    }

    @Override
    public BundleStatus checkBundleStatus() {
        // TODO 后续加入heart-check后通过健康检查和承载量来进行状态实时更新
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
