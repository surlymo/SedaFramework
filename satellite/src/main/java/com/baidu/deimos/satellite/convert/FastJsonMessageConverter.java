package com.baidu.deimos.satellite.convert;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import com.alibaba.fastjson.JSON;

/**
 * 转换类型模板。采用fastjson进行数据序列化和反序列化。
 * 
 * Created on Jan 15, 2014 2:22:51 PM
 * @author chenchao03
 * @version 1.0
 * @since 1.0
 */
public class FastJsonMessageConverter extends AbstractMessageConverter {

    public static final String DEFAULT_CHARSET = "GBK";

    private volatile String defaultCharset = DEFAULT_CHARSET;

    /**
     * 构造函数，直接调用上层
     */
    public FastJsonMessageConverter() {
        super();
    }

    /**
     * 设置默认字符集
     * 
     * @param defaultCharset
     *      默认字符集
     * @author chenchao03
     * @date Jan 14, 2014
     */
    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = (defaultCharset != null) ? defaultCharset : DEFAULT_CHARSET;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return null;
    }

    /**
     * 反序列化过程
     * 
     * @param message
     *      消息内容
     * @param t
     *      要转换的类型
     * @return
     *      转换结果
     * @author chenchao03
     * @date Jan 14, 2014
     */
    @SuppressWarnings("unchecked")
    public <T> T fromMessage(Message message, T t) {
        String json = "";
        try {
            json = new String(message.getBody(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return (T) JSON.parseObject(json, t.getClass());
    }

    /**
     * 序列化过程
     * @throws MessageConversionException
     *      消息转换异常
     */
    @Override
    protected Message createMessage(Object objectToConvert, MessageProperties messageProperties)
            throws MessageConversionException {
        byte[] bytes = null;
        try {
            String jsonString = JSON.toJSONString(objectToConvert);
            bytes = jsonString.getBytes(this.defaultCharset);
        } catch (UnsupportedEncodingException e) {
            throw new MessageConversionException("Failed to convert Message content", e);
        }
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding(this.defaultCharset);
        if (bytes != null) {
            messageProperties.setContentLength(bytes.length);
        }
        return new Message(bytes, messageProperties);

    }
}