package com.baidu.deimos.satellite.core.decision;

import com.baidu.deimos.satellite.constant.BundleStatus;
import com.baidu.deimos.satellite.constant.SyncType;
import com.baidu.deimos.satellite.constant.TimelinessType;

/**
 * bundle风格决策器。由于不同bundle可以定制不一样的决策器。
 * 所以以接口形式提供 此处还有健康检查机制、和监控bundle机制，都可以糅合到这里来写
 * 
 * Created on Jan 15, 2014 2:18:09 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public interface SatBundleStyleDecision {

    /**
     * 检验bundle状态
     * @return
     * @author chenchao03
     * @date Jan 14, 2014
     * @return BundleStatus
     */
    BundleStatus checkBundleStatus();

    /**
     * 该bundle的激活状态。激活/待激活
     * @return
     * @author chenchao03
     * @date Jan 14, 2014
     * @return boolean
     */
    boolean isActived();

    /**
     * 该bundle的时效性类型。实时/定时
     * @return
     * @author chenchao03
     * @date Jan 14, 2014
     * @return TimelinessType
     */
    TimelinessType getTimelinesstype();

    /**
     * 该bundle的同步类型。同步/异步
     * @return
     *      返回bundle的同步类型
     * @author chenchao03
     * @date Jan 14, 2014
     * @returnType SyncType
     */
    SyncType getSyncType();

    /**
     * 健康检查
     * 
     * @author chenchao03
     * @date Jan 14, 2014
     * @returnType void
     */
    void healthycheck();
}
