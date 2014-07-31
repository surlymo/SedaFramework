package com.baidu.deimos.satellite.constant;

import com.baidu.deimos.satellite.api.constant.Infotype;

/**
 * ����������ã��Ժ���������ֻ��Ҫ�ڴ˴������ز������ɡ�
 * 
 * Created on Jan 15, 2014 2:20:32 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public enum AlarmCode {

    // �ܴ��󱨾�����
    ALL(Infotype.INFOTYPE_ALARM_ALL, 500, "˹���ǣ��ܴ�����̫���ˣ�"),

    // API������󱨾�����
    API(Infotype.INFOTYPE_ALARM_API, 500, "������ڣ�API����Ƶ��̫���ˣ�"),

    // δ�������log�ı�������
    ERROR(Infotype.INFOTYPE_ALARM_ERROR, 500, "˹����ɭ,δ���������������Ŷ��"),

    // ���쳣�ı�������
    EXCEPTION(Infotype.INFOTYPE_ALARM_EXCEPTION, 500, "˹����ɭ, ��̫���쳣������"),

    // �������adcore��mars��market���������󱨾�����
    SERVICE(Infotype.INFOTYPE_ALARM_SERVICE, 500, "������ڣ���������񽻻�����̫���˿���"),

    // STP������󱨾�����
    STP(Infotype.INFOTYPE_ALARM_STP, 500, "������, ��STP���˵ı���̫��������"),

    // �̳߳ر�����ֵ
    THREADPOOL(Infotype.INFOTYPE_ALARM_THREADPOOL, 500, "˹����ɭ, �̳߳����ӱ��ܾ�����̫���ˣ�");

    private AlarmCode(Infotype infotype, Integer limit, String content) {
        this.infotype = infotype;
        this.limit = limit;
        this.content = content;
    }

    public Infotype infotype;
    public Integer limit;
    public String content;
}
