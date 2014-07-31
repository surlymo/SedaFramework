package com.baidu.deimos.satellite.constant;

/**
 * 常量定义
 * 
 * Created on Jan 15, 2014 2:21:00 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class SatConstant {
    
    // 以下为timer传递给执行器的时候作为key存储对应数据
    public static final String DATA = "DATA";
    public static final String CARRIER = "CARRIER";
    public static final String EXECUTOR = "EXECUTOR";

    // 默认采用的交换机
    public static String DEFAULT_EXCHANGE = "deimos-common";

    // 通用收集器数据缓存长度
    public static int LOG_BATCH_SIZE = 100;

    // 总错误缓存上限
    public static int ALL_ALARM_LMT = 100;

    // 总错误报警内容
    public static String ALL_ALARM_CONTENT = "总错误数太高了！";

    // 错误log报警阈值
    public static int ERROR_LOG_ALARM_LMT = 100;

    // 错误log的报警内容
    public static String ERROR_LOG_ALARM_CONTENT = "斯眯麻森,未分类错误数过高了哦！";

    // 抛异常报警阈值
    public static int EXCEPTION_ALARM_LMT = 100;

    // 抛异常的报警内容
    public static String EXCEPTION_ALARM_CONTENT = "斯眯麻森, 抛太多异常了啦！";

    // 线程池被报警阈值
    public static int POOL_REJECT_ALARM_LMT = 100;

    // 线程池报警阈值
    public static String POOL_REJECT_ALARM_CONTENT = "斯眯麻森, 线程池连接被拒绝报警太多了！";

    // STP请求错误报警阈值
    public static int STP_ALARM_LMT = 100;

    // STP请求错误报警内容
    public static String STP_ALARM_CONTENT = "麻撒卡, 和STP那厮的报警太多了啦！";

    // 其余服务（adcore、mars、market）交互错误报警阈值
    public static int SERVER_ALARM_LMT = 100;

    // 其余服务（adcore、mars、market）交互错误报警内容
    public static String SERVER_ALARM_CONTENT = "空你机挖，和其余服务交互报警太多了咯！";

    // API服务报警阈值
    public static int API_ALARM_LMT = 100;

    // API服务错误报警内容
    public static String API_ALARM_CONTENT = "空你机挖，API请求频率太高了！";

    /**
     * @return the dEFAULT_EXCHANGE
     */
    public static String getDEFAULT_EXCHANGE() {
        return DEFAULT_EXCHANGE;
    }

    /**
     * @param dEFAULT_EXCHANGE the dEFAULT_EXCHANGE to set
     */
    public static void setDEFAULT_EXCHANGE(String dEFAULT_EXCHANGE) {
        DEFAULT_EXCHANGE = dEFAULT_EXCHANGE;
    }

    /**
     * @return the lOG_BATCH_SIZE
     */
    public static int getLOG_BATCH_SIZE() {
        return LOG_BATCH_SIZE;
    }

    /**
     * @param lOG_BATCH_SIZE the lOG_BATCH_SIZE to set
     */
    public static void setLOG_BATCH_SIZE(int lOG_BATCH_SIZE) {
        LOG_BATCH_SIZE = lOG_BATCH_SIZE;
    }

    /**
     * @return the aLL_ALARM_LMT
     */
    public static int getALL_ALARM_LMT() {
        return ALL_ALARM_LMT;
    }

    /**
     * @param aLL_ALARM_LMT the aLL_ALARM_LMT to set
     */
    public static void setALL_ALARM_LMT(int aLL_ALARM_LMT) {
        ALL_ALARM_LMT = aLL_ALARM_LMT;
    }

    /**
     * @return the aLL_ALARM_CONTENT
     */
    public static String getALL_ALARM_CONTENT() {
        return ALL_ALARM_CONTENT;
    }

    /**
     * @param aLL_ALARM_CONTENT the aLL_ALARM_CONTENT to set
     */
    public static void setALL_ALARM_CONTENT(String aLL_ALARM_CONTENT) {
        ALL_ALARM_CONTENT = aLL_ALARM_CONTENT;
    }

    /**
     * @return the eRROR_LOG_ALARM_LMT
     */
    public static int getERROR_LOG_ALARM_LMT() {
        return ERROR_LOG_ALARM_LMT;
    }

    /**
     * @param eRROR_LOG_ALARM_LMT the eRROR_LOG_ALARM_LMT to set
     */
    public static void setERROR_LOG_ALARM_LMT(int eRROR_LOG_ALARM_LMT) {
        ERROR_LOG_ALARM_LMT = eRROR_LOG_ALARM_LMT;
    }

    /**
     * @return the eRROR_LOG_ALARM_CONTENT
     */
    public static String getERROR_LOG_ALARM_CONTENT() {
        return ERROR_LOG_ALARM_CONTENT;
    }

    /**
     * @param eRROR_LOG_ALARM_CONTENT the eRROR_LOG_ALARM_CONTENT to set
     */
    public static void setERROR_LOG_ALARM_CONTENT(String eRROR_LOG_ALARM_CONTENT) {
        ERROR_LOG_ALARM_CONTENT = eRROR_LOG_ALARM_CONTENT;
    }

    /**
     * @return the eXCEPTION_ALARM_LMT
     */
    public static int getEXCEPTION_ALARM_LMT() {
        return EXCEPTION_ALARM_LMT;
    }

    /**
     * @param eXCEPTION_ALARM_LMT the eXCEPTION_ALARM_LMT to set
     */
    public static void setEXCEPTION_ALARM_LMT(int eXCEPTION_ALARM_LMT) {
        EXCEPTION_ALARM_LMT = eXCEPTION_ALARM_LMT;
    }

    /**
     * @return the eXCEPTION_ALARM_CONTENT
     */
    public static String getEXCEPTION_ALARM_CONTENT() {
        return EXCEPTION_ALARM_CONTENT;
    }

    /**
     * @param eXCEPTION_ALARM_CONTENT the eXCEPTION_ALARM_CONTENT to set
     */
    public static void setEXCEPTION_ALARM_CONTENT(String eXCEPTION_ALARM_CONTENT) {
        EXCEPTION_ALARM_CONTENT = eXCEPTION_ALARM_CONTENT;
    }

    /**
     * @return the pOOL_REJECT_ALARM_LMT
     */
    public static int getPOOL_REJECT_ALARM_LMT() {
        return POOL_REJECT_ALARM_LMT;
    }

    /**
     * @param pOOL_REJECT_ALARM_LMT the pOOL_REJECT_ALARM_LMT to set
     */
    public static void setPOOL_REJECT_ALARM_LMT(int pOOL_REJECT_ALARM_LMT) {
        POOL_REJECT_ALARM_LMT = pOOL_REJECT_ALARM_LMT;
    }

    /**
     * @return the pOOL_REJECT_ALARM_CONTENT
     */
    public static String getPOOL_REJECT_ALARM_CONTENT() {
        return POOL_REJECT_ALARM_CONTENT;
    }

    /**
     * @param pOOL_REJECT_ALARM_CONTENT the pOOL_REJECT_ALARM_CONTENT to set
     */
    public static void setPOOL_REJECT_ALARM_CONTENT(String pOOL_REJECT_ALARM_CONTENT) {
        POOL_REJECT_ALARM_CONTENT = pOOL_REJECT_ALARM_CONTENT;
    }

    /**
     * @return the sTP_ALARM_LMT
     */
    public static int getSTP_ALARM_LMT() {
        return STP_ALARM_LMT;
    }

    /**
     * @param sTP_ALARM_LMT the sTP_ALARM_LMT to set
     */
    public static void setSTP_ALARM_LMT(int sTP_ALARM_LMT) {
        STP_ALARM_LMT = sTP_ALARM_LMT;
    }

    /**
     * @return the sTP_ALARM_CONTENT
     */
    public static String getSTP_ALARM_CONTENT() {
        return STP_ALARM_CONTENT;
    }

    /**
     * @param sTP_ALARM_CONTENT the sTP_ALARM_CONTENT to set
     */
    public static void setSTP_ALARM_CONTENT(String sTP_ALARM_CONTENT) {
        STP_ALARM_CONTENT = sTP_ALARM_CONTENT;
    }

    /**
     * @return the sERVER_ALARM_LMT
     */
    public static int getSERVER_ALARM_LMT() {
        return SERVER_ALARM_LMT;
    }

    /**
     * @param sERVER_ALARM_LMT the sERVER_ALARM_LMT to set
     */
    public static void setSERVER_ALARM_LMT(int sERVER_ALARM_LMT) {
        SERVER_ALARM_LMT = sERVER_ALARM_LMT;
    }

    /**
     * @return the sERVER_ALARM_CONTENT
     */
    public static String getSERVER_ALARM_CONTENT() {
        return SERVER_ALARM_CONTENT;
    }

    /**
     * @param sERVER_ALARM_CONTENT the sERVER_ALARM_CONTENT to set
     */
    public static void setSERVER_ALARM_CONTENT(String sERVER_ALARM_CONTENT) {
        SERVER_ALARM_CONTENT = sERVER_ALARM_CONTENT;
    }

    /**
     * @return the aPI_ALARM_LMT
     */
    public static int getAPI_ALARM_LMT() {
        return API_ALARM_LMT;
    }

    /**
     * @param aPI_ALARM_LMT the aPI_ALARM_LMT to set
     */
    public static void setAPI_ALARM_LMT(int aPI_ALARM_LMT) {
        API_ALARM_LMT = aPI_ALARM_LMT;
    }

    /**
     * @return the aPI_ALARM_CONTENT
     */
    public static String getAPI_ALARM_CONTENT() {
        return API_ALARM_CONTENT;
    }

    /**
     * @param aPI_ALARM_CONTENT the aPI_ALARM_CONTENT to set
     */
    public static void setAPI_ALARM_CONTENT(String aPI_ALARM_CONTENT) {
        API_ALARM_CONTENT = aPI_ALARM_CONTENT;
    }
}
