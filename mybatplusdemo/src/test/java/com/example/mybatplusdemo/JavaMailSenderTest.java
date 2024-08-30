package com.example.mybatplusdemo;


import cn.hutool.extra.template.engine.velocity.VelocityEngine;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * https://www.jb51.net/program/308472xx7.htm
 */

@SpringBootTest
public class JavaMailSenderTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    //简单的邮件发送
    @Test
    public void sendSimpleMail() throws Exception {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1724059570@qq.com");
        message.setTo("1724059570@qq.com");
        message.setSubject("主题：简单邮件");
        message.setText("简单的邮件内容");

        mailSender.send(message);
    }

    //发送嵌入静态资源
    @Test
    public void sendInlineMail() throws Exception {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("28****70@qq.com");
        helper.setTo("28****70@qq.com");
        helper.setSubject("主题：嵌入静态资源");
        helper.setText("<html><body><img src=\"cid:weixin\" ></body></html>", true);

        FileSystemResource file = new FileSystemResource(new File("weixin.jpg"));
        helper.addInline("weixin", file);

        mailSender.send(mimeMessage);
    }

    //发送模版附件
//    @Test
//    public void sendTemplateMail() throws Exception {
//
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//        helper.setFrom("28****70@qq.com");
//        helper.setTo("28****70@qq.com");
//        helper.setSubject("主题：模板邮件");
//
//        Map<String, Object> model = new HashedMap();
//        model.put("username", "miaow");
//        String text = VelocityEngineUtils.mergeTemplateIntoString(
//                velocityEngine, "template.vm", "UTF-8", model);
//        helper.setText(text, true);
//
//        mailSender.send(mimeMessage);
//    }
}
