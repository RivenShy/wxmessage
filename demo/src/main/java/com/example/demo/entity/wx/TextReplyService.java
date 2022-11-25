package com.example.demo.entity.wx;

import com.example.demo.controller.WxOfficialAccountController;
import com.example.demo.util.WechatMessageUtils;
import org.apache.log4j.Logger;

import java.util.Map;

//@Service
public class TextReplyService {

    private static Logger logger = Logger.getLogger(WxOfficialAccountController.class);


    public static final String FROM_USER_NAME = "FromUserName";
    public static final String TO_USER_NAME = "ToUserName";
    public static final String CONTENT = "Content";

//    @Autowired
//    private WechatKeywordDao wechatKeywordDao;
//
//    @Autowired
//    private WechatMsgRecordDao wechatMsgRecordDao;

    /**
     * 自动回复文本内容
     *
     * @param requestMap
     * @return
     */
    public String reply(Map<String, String> requestMap) {
        String wechatId = requestMap.get(FROM_USER_NAME);
        String gongzhonghaoId = requestMap.get(TO_USER_NAME);

        TextMessage textMessage = WechatMessageUtils.getDefaultTextMessage(wechatId, gongzhonghaoId);

        String content = requestMap.get(CONTENT);
        logger.info("content=" + content);
        if(content.equals("百度")) {
            textMessage.setContent("百度:https://www.baidu.com/");
        } else if("hello".equals(content)) {
            textMessage.setContent("hi");
        } else {
            // 不回复
            return null;
        }
        return WechatMessageUtils.textMessageToXml(textMessage);
    }

}
