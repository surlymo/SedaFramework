/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.util;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.baidu.deimos.satellite.exception.SatAmqpException;

/**
 * @title MailService
 * @description �ʼ�����
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
@Component
public class MailService {

    /**
     * �����ʼ�
     * @param html ����
     * @param from �ʼ�������
     * @param to �ʼ��ռ���
     * @param cc �ʼ�������
     * @param title ����
     * @param isHtml �����Ƿ���Ҫhtml�淶
     * @throws MessagingException ��Ϣ��װʱ����쳣
     * @throws SatAmqpException amqpͨ���쳣
     *     
     */
    public void sendMail(String html, String from, String[] to, String[] cc, String title, boolean isHtml)
            throws SatAmqpException, MessagingException {
        if (checkValid(html, from, to, cc, title)) {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(PropertyReader.getProperty("mail.smtp.host"));
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, "gbk");
            messageHelper.setFrom(from);
            messageHelper.setTo(to);
            messageHelper.setCc(cc);
            messageHelper.setSubject(title);
            messageHelper.setText(html, isHtml);
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", PropertyReader.getProperty("mail.smtp.auth"));
            sender.setJavaMailProperties(prop);
            sender.send(message);
        }
    }

    /**
     * �����������У��
     * @param html �ʼ�����
     * @param from ��Դ
     * @param to �ռ���
     * @param cc ����
     * @param title ����
     * @return У����ȷ���
     */
    private boolean checkValid(String html, String from, String[] to, String[] cc, String title) {
        return StringUtils.isNotBlank(html) && StringUtils.isNotBlank(from) && StringUtils.isNotBlank(title)
                && to != null && to.length > 0;
    }
}
