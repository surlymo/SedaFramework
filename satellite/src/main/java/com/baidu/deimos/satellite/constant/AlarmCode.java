package com.baidu.deimos.satellite.constant;

import com.baidu.deimos.satellite.api.constant.Infotype;

/**
 * 报警相关配置，以后如有新增只需要在此处添加相关参数即可。
 * 
 * Created on Jan 15, 2014 2:20:32 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public enum AlarmCode {

    // 总错误报警内容
    ALL(Infotype.INFOTYPE_ALARM_ALL, 500, "斯麻那，总错误数太高了！"),

    // API服务错误报警内容
    API(Infotype.INFOTYPE_ALARM_API, 500, "空你机挖，API请求频率太高了！"),

    // 未分类错误log的报警内容
    ERROR(Infotype.INFOTYPE_ALARM_ERROR, 500, "斯眯麻森,未分类错误数过高了哦！"),

    // 抛异常的报警内容
    EXCEPTION(Infotype.INFOTYPE_ALARM_EXCEPTION, 500, "斯眯麻森, 抛太多异常了啦！"),

    // 其余服务（adcore、mars、market）交互错误报警内容
    SERVICE(Infotype.INFOTYPE_ALARM_SERVICE, 500, "空你机挖，和其余服务交互报警太多了咯！"),

    // STP请求错误报警内容
    STP(Infotype.INFOTYPE_ALARM_STP, 500, "麻撒卡, 和STP那厮的报警太多了啦！"),

    // 线程池报警阈值
    THREADPOOL(Infotype.INFOTYPE_ALARM_THREADPOOL, 500, "斯眯麻森, 线程池连接被拒绝报警太多了！");

    private AlarmCode(Infotype infotype, Integer limit, String content) {
        this.infotype = infotype;
        this.limit = limit;
        this.content = content;
    }

    public Infotype infotype;
    public Integer limit;
    public String content;
}
