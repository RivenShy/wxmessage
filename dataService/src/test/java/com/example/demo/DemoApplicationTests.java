package com.example.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.controller.WxOfficialAccountController;
import com.example.demo.entity.Message;
import com.example.demo.entity.MessageType;
import com.example.demo.entity.ScheduleJob;
import com.example.demo.entity.UserDepm;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.service.MessageTypeService;
import com.example.demo.service.UserDepmService;
import com.example.demo.util.WxUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.quartz.JobBuilder.newJob;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private UserDepmService userDepmService;

	@Autowired
	private MessageTypeService messageTypeService;

	@Autowired
	private MessageMapper messageMapper;

	@Test
	void contextLoads() {
	}

	@Test
	public void send() {
		String corp_id = "ww02ef9c92029980d4";
		String secret = "AlWI5itq80R-iBwgdYDgu-3BVUg-p7d-93u_tlWpO68";
//		String resulturl  = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ww02ef9c92029980d4&amp;corpsecret=AlWI5itq80R-iBwgdYDgu-3BVUg-p7d-93u_tlWpO68";
		String resulturl  = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ww02ef9c92029980d4&corpsecret=AlWI5itq80R-iBwgdYDgu-3BVUg-p7d-93u_tlWpO68";

		String param=resulturl;    //请求的发送的参数
		System.out.println("param:" + param);
		try {
			Map map = new HashMap();
			URL url = new URL(param);
			//打开和url之间的连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			/**设置URLConnection的参数和普通的请求属性****start***/
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("content-type","application/json");
			/**设置URLConnection的参数和普通的请求属性****end***/
			//设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
			//最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
			//post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");//GET和POST必须全大写
			/**GET方法请求*****start*/
			conn.connect();
			/**GET方法请求*****end*/
			//获取URLConnection对象对应的输入流
			InputStream is = conn.getInputStream();
			//构造一个字符流缓存
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = "";
			while ((str = br.readLine()) != null) {
				str=new String(str.getBytes(),"UTF-8");//解决中文乱码问题
				System.out.println("str:" + str);
				JSONObject parseObject = JSONArray.parseObject(str);
				//调用腾讯接口返回回来的openid
				String access_token = (String) parseObject.get("access_token");
				//调用腾讯接口返回回来的openid
				Integer expires_in = (Integer)  parseObject.get("expires_in");
				map.put("access_token",access_token);
				map.put("expires_in",expires_in);
				System.out.println("access_token:" + access_token);
				System.out.println("expires_in:" + expires_in);
			}
			//关闭流
			is.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	@Test
	public void home(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//		req.getParameter("")
		String corp_id = "ww02ef9c92029980d4";
		String secret = "AlWI5itq80R-iBwgdYDgu-3BVUg-p7d-93u_tlWpO68";
		String redirect_uri = "http://localhost/app";
//		String login_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${corp_id}&redirect_uri=${redirect_uri}&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
		String login_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
		login_url = String.format(login_url, corp_id, redirect_uri);
		resp.sendRedirect(login_url);
//		return String.format("Hello %s!", name);
	}

	@Test
	public void testMap() {
		String agent_id = "1000063";
		String msgtype = "text";
//		String text = "text";
		String userid = "mongo";
		JSONObject map = new JSONObject();
		Map<String, String> maptext = new HashMap<>();
		JSONObject json = new JSONObject();
		json.put("content", "Hello World," + userid);
		maptext.put("content", "Hello World," + userid);
		map.put("agentid", agent_id);
		map.put("touser", userid);
		map.put("msgtype", msgtype);
//		map.put("text", maptext.toString());
		map.put("text", json);
		System.out.println("map.toString():" + map.toString());
	}

	@Test
	public void testGetToken() {
		String appid = "wx226ffc0b68fa17e9";
		String appsecret = "4a4037b79e0390da5a4d8cb8ff5014f0";
//		有效期两小时
		String accessToken = null;
		String expiresIn = null;
		String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
		getTokenUrl = String.format(getTokenUrl, appid, appsecret);
		JSONObject jsonObject = WxUtil.getDataFromWxServer(getTokenUrl);
		String errcode = jsonObject.getString("errcode");
		if(errcode == null) {
			System.out.println("getToken请求微信服务器成功");
			accessToken = jsonObject.getString("access_token");
			expiresIn = jsonObject.getString("expires_in");
			System.out.println("accessToken:" + accessToken);
			System.out.println("expiresIn:" + expiresIn);
		} else {
			String errmsg = jsonObject.getString("errmsg");
			System.out.println("getToken请求微信服务器失败，errcode:" + errcode + ",errmsg:" + errmsg);
		}
	}

	@Test
	public void sendMsgToUser() {
		String openid = "oa9m45rV0-lXYLO064zo-BJBNkSE";
		String template_id = "Co1-n9VISkwCUjCLjn34am3QCBSqY2SqIWCYzWHVJlE";
		String url = "http://weixin.qq.com/download";
		//
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("touser", openid);
		jsonObject.put("template_id", template_id);
		jsonObject.put("url", url);
		JSONObject jsonObject2 = new JSONObject();
		//
		JSONObject jsonObjectFirst = new JSONObject();
		jsonObjectFirst.put("value", "您有一个待审批事项");
		jsonObjectFirst.put("color", "#173177");
		jsonObject2.put("first", jsonObjectFirst);
		//
		JSONObject jsonObjectKeyword1 = new JSONObject();
		jsonObjectKeyword1.put("value", "JP1409010001");
		jsonObjectKeyword1.put("color", "#173177");
		jsonObject2.put("keyword1", jsonObjectKeyword1);
		//
		JSONObject jsonObjectKeyword2 = new JSONObject();
		jsonObjectKeyword2.put("value", "2014-09-01");
		jsonObjectKeyword2.put("color", "#173177");
		jsonObject2.put("keyword2", jsonObjectKeyword2);
		//
		JSONObject jsonObjectKeyword3 = new JSONObject();
		jsonObjectKeyword3.put("value", "张三");
		jsonObjectKeyword3.put("color", "#173177");
		jsonObject2.put("keyword3", jsonObjectKeyword3);
		//
		JSONObject jsonObjectKeyword4 = new JSONObject();
		jsonObjectKeyword4.put("value", "财务部");
		jsonObjectKeyword4.put("color", "#173177");
		jsonObject2.put("keyword4", jsonObjectKeyword4);
		//
		JSONObject jsonObjectKeyword5 = new JSONObject();
		jsonObjectKeyword5.put("value", "申请一部笔记本电脑");
		jsonObjectKeyword5.put("color", "#173177");
		jsonObject2.put("keyword5", jsonObjectKeyword5);
		//
		JSONObject jsonObjectRemark = new JSONObject();
		jsonObjectRemark.put("value", "回复 OK 直接批准");
		jsonObjectRemark.put("color", "#173177");
		jsonObject2.put("remark", jsonObjectRemark);
		//
		jsonObject.put("data", jsonObject2);
		//
		String accessToken = "62_TaBhppNdn6pWxgZsOCgRTvT5pD9s6z3NEKPduXrmkbkdzbHeNaoOQV-r3gg5PFLpMVtFdLNx7zv9e0AS8PJtC-9C7MEz727TvOJKypqxvvNCtQ10lRmnutYQHpeVj9rj940QeQI9TwnSFrP7OAPhAJASET";
		String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
		sendMsgUrl = String.format(sendMsgUrl, accessToken);
		System.out.println("sendMsgUrl:" + sendMsgUrl);
		System.out.println("jsonObject:" + jsonObject.toString());
		JSONObject wxResult = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
		String errcode = wxResult.getString("errcode");
		String errmsg = wxResult.getString("errmsg");
		String msgid = wxResult.getString("msgid");
		System.out.println("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
	}

	@Test
	public void getFansList() {
		String accessToken = "";
		String next_openid = "";
		String getFansListUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s&next_openid=%s";
		getFansListUrl = String.format(getFansListUrl, accessToken, next_openid);
		JSONObject jsonObject = WxUtil.getDataFromWxServer(getFansListUrl);
		String errcode = jsonObject.getString("errcode");
		if(errcode == null) {
			System.out.println("getToken请求微信服务器成功");
			int total = jsonObject.getInteger("total");
			int count = jsonObject.getInteger("count");
			String data = jsonObject.getString("data");
			System.out.println("total:" + total);
			System.out.println("count:" + count);
			System.out.println("data:" + data);
		} else {
			String errmsg = jsonObject.getString("errmsg");
			System.out.println("getFansList请求微信服务器失败，errcode:" + errcode + ",errmsg:" + errmsg);
		}
	}

	@Test
	public void createTicket() {
		String accessToken = "62_pzOl3ITpWqva_5NZ9oj8GXDEuoZ0qCV3xMIbNsrYbBPTu0ZKN5p1GVHzsN0XCB96VFBdKlUtC9wWvzl_az92sTolgddWZMbWGzGhdNhrKk4nZ8pLMm93ENo0IgTey_-V8jvKprH5flD_eCCBXKKbAHATGG";
		String createTempTicketUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
		createTempTicketUrl = String.format(createTempTicketUrl, accessToken);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("expire_seconds", 604800);
//		jsonObject.put("action_name", "QR_SCENE");
		jsonObject.put("action_name", "QR_STR_SCENE");
		JSONObject jsonObjectScene = new JSONObject();
		JSONObject jsonObjectSceneId = new JSONObject();
//		jsonObjectSceneId.put("scene_id", 123);
//		jsonObjectSceneId.put("scene_id", 1234567890);
		jsonObjectSceneId.put("scene_str", "0123456789012345678901234567890123456789012345678901234567890123");
//		jsonObjectSceneId.put("scene_str", "012345678901234567890123456789");
//		jsonObjectSceneId.put("scene_str", "test");
//		jsonObjectSceneId.put("id", "11111111111111111");
		jsonObjectScene.put("scene", jsonObjectSceneId);
		jsonObject.put("action_info", jsonObjectScene);
		System.out.println("jsonObject:" + jsonObject.toString());
		JSONObject result = WxUtil.postToWxServer(createTempTicketUrl, jsonObject.toString());
		String errcode = result.getString("errcode");
		if(errcode == null) {
			String ticket = result.getString("ticket");
			String expire_seconds = result.getString("expire_seconds");
			String url = result.getString("url");
			System.out.println("ticket:" + ticket);
			System.out.println("expire_seconds:" + expire_seconds);
			System.out.println("url:" + url);
			// 根据ticket换取二维码
			try {
				ticket = URLEncoder.encode(ticket, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String getQRcodeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
			getQRcodeUrl = String.format(getQRcodeUrl, ticket);
			System.out.println("getQRcodeUrl:" + getQRcodeUrl);

//			HttpClient httpClient = HttpClientBuilder.create().build();
//			//
//			try {
//				HttpGet httpGet = new HttpGet(getQRcodeUrl);
//				HttpResponse response = httpClient.execute(httpGet);// 接收client执行的结果
//				HttpEntity entity = response.getEntity();
//				if (entity != null) {
//					File qrCodePictureSavePath = new File("d:/qrcode/" + new Random().nextInt() +".jpg");
//					OutputStream os = new FileOutputStream(qrCodePictureSavePath);
//					entity.writeTo(os);
//				} else {
//					System.out.println("向微信服务器发送Get请求发生错误！");
//				}
//			} catch (Exception e) {
//				System.out.println("向微信服务器发送Get请求发生错误：" + e.getMessage());
//			}
		} else {
			String errmsg = jsonObject.getString("errmsg");
			System.out.println("createTicket请求微信服务器失败，errcode:" + errcode + ",errmsg:" + errmsg);
		}
	}

	@Test
	public void parseXML() throws DocumentException {
		String str = "<xml><ToUserName><![CDATA[gh_2f933163fbbe]]></ToUserName>\n" +
				"<FromUserName><![CDATA[oa9m45sEj_dDR9FGoeoGmx7_M21o]]></FromUserName>\n" +
				"<CreateTime>1667287080</CreateTime>\n" +
				"<MsgType><![CDATA[event]]></MsgType>\n" +
				"<Event><![CDATA[SCAN]]></Event>\n" +
				"<EventKey><![CDATA[0123456789012345678901234567890123456789012345678901234567890123]]></EventKey>\n" +
				"<Ticket><![CDATA[gQF68DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyRTNiQlFHSHBlVUMxaC0yR3h6Y2cAAgT_x2BjAwSAOgkA]]></Ticket>\n" +
				"</xml>";

		Document doc = DocumentHelper.parseText(str);
		Element root = doc.getRootElement();
		String ToUserName = root.elementText("ToUserName");
		String FromUserName = root.elementText("FromUserName");
		String CreateTime = root.elementText("CreateTime");
		String MsgType = root.elementText("MsgType");
		String Event = root.elementText("Event");
		String EventKey = root.elementText("EventKey");

		System.out.println("ToUserName:" + ToUserName);
		System.out.println("FromUserName:" + FromUserName);
		System.out.println("CreateTime:" + CreateTime);
		System.out.println("MsgType:" + MsgType);
		System.out.println("Event:" + Event);
		System.out.println("EventKey:" + EventKey);
	}

	@Test
	public void sendOuthMsgToUser() {
		String openid = "oa9m45sEj_dDR9FGoeoGmx7_M21o";
		String template_id = "McTEif4kxJ-BNiLp1fMFYR8Ymzc5kJoujeIq1dhqL20";
		String REDIRECT_URI = "http://www.szwd.online//wx/outhPage/" + 1;
		String outhUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		outhUrl = String.format(outhUrl, WxOfficialAccountController.appid, REDIRECT_URI);
		String url = outhUrl;
		//
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("touser", openid);
		jsonObject.put("template_id", template_id);
		jsonObject.put("url", url);
		JSONObject jsonObject2 = new JSONObject();
		//
		JSONObject jsonObjectFirst = new JSONObject();
		jsonObjectFirst.put("value", "您好，由于您申请了工程项目管理系统绑定申请，现已生成授权申请。");
		jsonObjectFirst.put("color", "#173177");
		jsonObject2.put("first", jsonObjectFirst);
		//
		JSONObject jsonObjectKeyword1 = new JSONObject();
		jsonObjectKeyword1.put("value", "工程项目管理系统");
		jsonObjectKeyword1.put("color", "#173177");
		jsonObject2.put("keyword1", jsonObjectKeyword1);
		//
		JSONObject jsonObjectKeyword2 = new JSONObject();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		jsonObjectKeyword2.put("value", simpleDateFormat.format(new Date()));
		jsonObjectKeyword2.put("color", "#173177");
		jsonObject2.put("keyword2", jsonObjectKeyword2);
		//
		JSONObject jsonObjectRemark = new JSONObject();
		jsonObjectRemark.put("value", "请点击详情进行授权操作");
		jsonObjectRemark.put("color", "#173177");
		jsonObject2.put("remark", jsonObjectRemark);
		//
		jsonObject.put("data", jsonObject2);
		//
//		String accessToken = WxUtil.getAccessToken();
		String accessToken = "62_1Q1ewVp6tWH7J73zidPos5PEJerZEKeeTNuWYih-Duqep3Rgm6w-J83vq91_lSPZ-UJRcMVDQhmD5U2BsYdxIlAeCCFT72EpXcZxizyV4FdQkemMGnrGbBjBW3jSRlY5OqJwq20izZ8dFQD_TPUhAHAHDI";
		String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
		sendMsgUrl = String.format(sendMsgUrl, accessToken);
		System.out.println("sendMsgUrl:" + sendMsgUrl);
		System.out.println("jsonObject:" + jsonObject.toString());
		JSONObject wxResult = WxUtil.postToWxServer(sendMsgUrl, jsonObject.toString());
		String errcode = wxResult.getString("errcode");
		String errmsg = wxResult.getString("errmsg");
		String msgid = wxResult.getString("msgid");
		System.out.println("errcode:" + errcode + ", errmsg:" + errmsg + ", msgid:" + msgid);
	}

	@Test
	public void test() throws UnsupportedEncodingException {

		String str = "qrscene_1";
		String[] strArr = str.split("_");
		System.out.println("strArr[1]:" + strArr[1]);
	}

	@Test
	public void testGetUserDepmList() {
		List<UserDepm> list = userDepmService.list();
		for(UserDepm userDepm : list) {
			System.out.println(userDepm);
		}
	}

	@Test
	public void testGetMessgeTypeList() {
		List<MessageType> list = messageTypeService.list();
		System.out.println(list);
	}

	@Test
	public void testQuartz() {
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity("trigger", "group")
					.withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?")).build();
			JobDetail job = newJob(ScheduleJob.class)
					.withIdentity("job", "group")
					.usingJobData("name", "testJob")
					.build();
			CronTrigger trigger2 = (CronTrigger) TriggerBuilder.newTrigger().withIdentity("trigger2", "group")
					.withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?")).build();
			JobDetail job2 = newJob(ScheduleJob.class)
					.withIdentity("job2", "group")
					.usingJobData("name", "testJob222222")
					.build();
			scheduler.scheduleJob(job, trigger);
			scheduler.scheduleJob(job2, trigger2);
			scheduler.start();
			Thread.sleep(28000);
			scheduler.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReurnMessageID() {
		Message message = new Message();
		message.setSendTime(new Date());
		message.setUserId(1);
		message.setMsgTypeId(1);
		message.setStatus(0);
		messageMapper.add(message);
		System.out.println("Message.getId():" + message.getId());
	}

	@Test
	public void testSendRemoteLoginMsg() {
		WxUtil.sendRemoteLoginMsg("oa9m45sEj_dDR9FGoeoGmx7_M21o", 1);
	}
}
