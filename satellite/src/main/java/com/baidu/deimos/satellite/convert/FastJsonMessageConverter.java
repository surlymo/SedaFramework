package com.baidu.deimos.satellite.convert;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import com.alibaba.fastjson.JSON;

/**
 * ת������ģ�塣����fastjson�����������л��ͷ����л���
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
     * ���캯����ֱ�ӵ����ϲ�
     */
    public FastJsonMessageConverter() {
        super();
    }

    /**
     * ����Ĭ���ַ���
     * 
     * @param defaultCharset
     *      Ĭ���ַ���
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
     * �����л�����
     * 
     * @param message
     *      ��Ϣ����
     * @param t
     *      Ҫת��������
     * @return
     *      ת�����
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
     * ���л�����
     * @throws MessageConversionException
     *      ��Ϣת���쳣
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