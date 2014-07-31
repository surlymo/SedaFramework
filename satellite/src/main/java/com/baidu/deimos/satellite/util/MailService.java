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
 * @description 邮件推送
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Feb 25, 2014 4:56:21 PM
 * @version 1.0
 */
@Component
public class MailService {

    /**
     * 推送邮件
     * @param html 内容
     * @param from 邮件发送者
     * @param to 邮件收件人
     * @param cc 邮件抄送人
     * @param title 标题
     * @param isHtml 内容是否需要html规范
     * @throws MessagingException 消息封装时候的异常
     * @throws SatAmqpException amqp通用异常
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
     * 进行输入参数校验
     * @param html 邮件内容
     * @param from 来源
     * @param to 收件人
     * @param cc 抄送
     * @param title 标题
     * @return 校验正确与否
     */
    private boolean checkValid(String html, String from, String[] to, String[] cc, String title) {
        return StringUtils.isNotBlank(html) && StringUtils.isNotBlank(from) && StringUtils.isNotBlank(title)
                && to != null && to.length > 0;
    }
}
