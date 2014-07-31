package com.baidu.deimos.satellite.core.decision;

import com.baidu.deimos.satellite.constant.DecisionVariety;
import com.baidu.deimos.satellite.core.SatVirtualBundle;

/**
 * bundle�������������ڲ�ͬbundle���Զ��Ʋ�һ���ľ������������Խӿ���ʽ�ṩ �˴����н��������ơ��ͼ��bundle���ƣ��������ۺϵ�������д
 * ��Ϊ���ǵ��Ժ�����еķֲ�ʽ���𣬴˴���������static�������洢ȫ�����е�bundle������
 * 
 * Created on Jan 15, 2014 2:18:55 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SatBundleStyleDecisionFactory {

    public static DecisionVariety DEFAULT_DECISION = DecisionVariety.FIXED_STRATEGY;

    /**
     * ��ѡĬ�Ͼ�����
     * 
     * @param bundle
     *      �����������bundle
     * @return
     *      ���ؾ�����
     * @author chenchao03
     * @date Jan 13, 2014
     */
    public static SatBundleStyleDecision getInstance(SatVirtualBundle bundle) {
        return getInstance(DEFAULT_DECISION, bundle);
    }

    /**
     * ��ѡ������
     * 
     * @param decisionVariety
     *      ����������
     * @param bundle
     *      �����������bundle
     * @return
     *      ���ؾ�����ʵ��
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
