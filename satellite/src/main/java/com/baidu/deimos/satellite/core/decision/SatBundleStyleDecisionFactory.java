package com.baidu.deimos.satellite.core.decision;

import com.baidu.deimos.satellite.constant.DecisionVariety;
import com.baidu.deimos.satellite.core.SatVirtualBundle;

/**
 * bundle风格决策器。由于不同bundle可以定制不一样的决策器。所以以接口形式提供 此处还有健康检查机制、和监控bundle机制，都可以糅合到这里来写
 * 因为考虑到以后可能有的分布式部署，此处不可以用static变量来存储全局所有的bundle决策器
 * 
 * Created on Jan 15, 2014 2:18:55 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SatBundleStyleDecisionFactory {

    public static DecisionVariety DEFAULT_DECISION = DecisionVariety.FIXED_STRATEGY;

    /**
     * 挑选默认决策器
     * 
     * @param bundle
     *      请求决策器的bundle
     * @return
     *      返回决策器
     * @author chenchao03
     * @date Jan 13, 2014
     */
    public static SatBundleStyleDecision getInstance(SatVirtualBundle bundle) {
        return getInstance(DEFAULT_DECISION, bundle);
    }

    /**
     * 挑选决策器
     * 
     * @param decisionVariety
     *      决策器类型
     * @param bundle
     *      请求决策器的bundle
     * @return
     *      返回决策器实例
     * @author chenchao03
     * @date Jan 13, 2014
     */
    public static SatBundleStyleDecision getInstance(DecisionVariety decisionVariety, SatVirtualBundle bundle) {
        switch (decisionVariety) {
        case FIXED_STRATEGY:
            return new SatFixedStrategyDecision(bundle);
        default:
            throw new RuntimeException("unsupported descision variety!");
        }
    }
}
