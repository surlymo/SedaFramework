/**
 * Baidu.com Inc.
 * Copyright (c) 2000-2014 All Rights Reserved.
 */
package com.baidu.deimos.satellite.workflow.pusher;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.baidu.deimos.satellite.api.bo.DeimosSatelliteRequest;
import com.baidu.deimos.satellite.api.constant.SatApiConstant;
import com.baidu.deimos.satellite.core.SatAmqpBundle;
import com.baidu.deimos.satellite.exception.SatAmqpException;
import com.baidu.deimos.satellite.util.MailService;

/**
 * @title EmailPusherBundle
 * @description ������ļ���db��ȥ��userid��Ӧ���ʼ����������ʼ�
 * @author Chao Chen(chenchao03@baidu.com)
 * @date Jan 15, 2014 2:35:32 PM
 * @version 1.0
 */
public class EmailPusherBundle extends SatAmqpBundle {
    private static final Logger LOGGER = Logger.getLogger(EmailPusherBundle.class);
    @Autowired
    private MailService mailService;

    @SuppressWarnings("unchecked")
    @Override
    public Object doWork(List<DeimosSatelliteRequest> msgList) throws SatAmqpException {
        for (DeimosSatelliteRequest metaMsg : msgList) {
            List<Map<String, String>> reqList = (List<Map<String, String>>) metaMsg.getRealData();
            // ������Ҫ��cache�л�ȡ��ǰ�Ѿ������˶��ٴ��������ظ�����
            // �Դ�������ݿ�ʼ�ּ�
            for (Map<String, String> realReq : reqList) {
                // ������
                String mailFrom = String.valueOf(realReq.get(SatApiConstant.MAIL_FROM));
                // �ռ���
                String mailTo = String.valueOf(realReq.get(SatApiConstant.MAIL_TO));
                // ������
                String mailCc = String.valueOf(realReq.get(SatApiConstant.MAIL_CC));
                // ����
                String mailContent = String.valueOf(realReq.get(SatApiConstant.MAIL_CONTENT));
                // ���ݸ�ʽ�Ƿ�Ϊhtml��
                String mailIsHtml = String.valueOf(realReq.get(SatApiConstant.MAIL_IS_HTML));
                // ����
                try {
                    mailService.sendMail(mailContent, mailFrom, mailTo.split(","), mailCc.split(","), mailContent,
                            Boolean.valueOf(mailIsHtml));
                } catch (MessagingException e) {
                    throw new SatAmqpException("error occur while dealing with email push", e);
                }

            }
        }
        LOGGER.info("email push step done.");
        return null;
    }
}
