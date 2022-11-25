package com.example.demo.util;


import com.example.demo.entity.wx.TextMessage;
import com.example.demo.entity.wx.WechatMsgTypeConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WechatMessageUtils {

    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>();

        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        try {
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点

            List<Element> elementList = root.elements();

            // 遍历所有子节点
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
        } finally {
            // 释放资源
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return map;
    }

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(TextMessage textMessage) {
        XSTREAM.alias("xml", textMessage.getClass());
        return XSTREAM.toXML(textMessage);
    }

//    /**
//     * 音乐消息对象转换成xml
//     *
//     * @param musicMessage 音乐消息对象
//     * @return xml
//     */
//    public static String musicMessageToXml(MusicMessage musicMessage) {
//        XSTREAM.alias("xml", musicMessage.getClass());
//        return XSTREAM.toXML(musicMessage);
//    }

//    /**
//     * 图文消息对象转换成xml
//     *
//     * @param newsMessage 图文消息对象
//     * @return xml
//     */
//    public static String newsMessageToXml(NewsMessage newsMessage) {
//        XSTREAM.alias("xml", newsMessage.getClass());
//        XSTREAM.alias("item", Article.class);
//        return XSTREAM.toXML(newsMessage);
//    }

    /**
     * 扩展xstream，使其支持CDATA块
     */
    private static final XStream XSTREAM = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                final boolean cdata = true;

                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    /**
     * 获取默认文本消息
     *
     * @param receiver     接收人
     * @param officialWxid 官方微信id
     * @return 文本消息
     */
    public static TextMessage getDefaultTextMessage(String receiver, String officialWxid) {
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(receiver);
        textMessage.setFromUserName(officialWxid);
        textMessage.setCreateTime(System.currentTimeMillis());
        textMessage.setMsgType(WechatMsgTypeConstant.MESSAGE_TYPE_TEXT);
        textMessage.setFuncFlag(0);

        return textMessage;
    }

}
