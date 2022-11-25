package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.controller.WxOfficialAccountController;
import com.example.demo.entity.*;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.service.MessageTypeService;
import com.example.demo.service.UserDepmService;
import com.example.demo.util.OkHttpUtil;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private UserDepmService userDepmService;

	@Autowired
	private MessageTypeService messageTypeService;

	@Autowired
	private MessageMapper messageMapper;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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

	@Test
	public void testOkHttp() throws ParseException {
		String responseData = OkHttpUtil.get("/pendingApproval/list", null);
		System.out.println("responseData:" + responseData);
		JSONObject jsonObject = JSONObject.parseObject(responseData);
		JSONArray jsonArrayData = jsonObject.getJSONArray("data");
		System.out.println(jsonArrayData);
		System.out.println("jsonArrayData.size():" + jsonArrayData.size());
		List<PendingApproval> pendingApprovalList = new ArrayList<>();
		for(int i = 0; i < jsonArrayData.size(); i++) {
			JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
//			System.out.print(jsonObjectPendingApproval.getString("codeid") + ",");
//			System.out.print(jsonObjectPendingApproval.getString("jobuser") + ",");
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			System.out.print(simpleDateFormat.format(jsonObjectPendingApproval.getDate("lastAuditTime")));
//			System.out.println();
			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
			pendingApproval.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
			pendingApproval.setTotalCount(jsonObjectPendingApproval.getInteger("totalCount"));
			pendingApprovalList.add(pendingApproval);
		}
		System.out.println("pendingApprovalList.size() = " + pendingApprovalList.size());
		for (PendingApproval pendingApproval : pendingApprovalList) {
			System.out.println(pendingApproval);
		}
	}

	@Test
	public void testImageToByte() {
//		byte[] data = WxUtil.imageToByte("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQH67zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyUTFKN1JDSHBlVUMxaU92WGh6YzgAAgQy5XFjAwSAOgkA");
//		System.out.println("data = " + data.toString());
//		WxUtil.byteToImage(data, "F:/fntTask/wxQRcode.jpg");
//		String str = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/wAALCAGuAa4BAREA/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/9oACAEBAAA/AP1ToooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooorlPil8UvDHwW8Can4y8Zan/Y3hvTfK+13v2eWfy/MlSJPkiVnOXkQcKcZyeATXgH/D0f9mL/AKKb/wCUDVP/AJGo/wCHo/7MX/RTf/KBqn/yNR/w9H/Zi/6Kb/5QNU/+RqP+Ho/7MX/RTf8Aygap/wDI1H/D0f8AZi/6Kb/5QNU/+RqP+Ho/7MX/AEU3/wAoGqf/ACNR/wAPR/2Yv+im/wDlA1T/AORqP+Ho/wCzF/0U3/ygap/8jV6r8C/2o/hh+0p/bf8AwrjxN/wkf9i+R9v/ANAurXyfO8zyv9fEm7PlSfdzjbzjIz6rRXyr/wAPR/2Yv+im/wDlA1T/AORqP+Ho/wCzF/0U3/ygap/8jUf8PR/2Yv8Aopv/AJQNU/8Akaj/AIej/sxf9FN/8oGqf/I1H/D0f9mL/opv/lA1T/5Go/4ej/sxf9FN/wDKBqn/AMjV7/8AC34peGPjT4E0zxl4N1P+2fDepeb9kvfs8sHmeXK8T/JKquMPG45UZxkcEGurrlPil8UvDHwW8Can4y8Zan/Y3hvTfK+13v2eWfy/MlSJPkiVnOXkQcKcZyeATXgH/D0f9mL/AKKb/wCU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";
//		jsonObject.put("expire_seconds", 604800);
//		jsonObject.put("action_name", "QR_STR_SCENE");
//		JSONObject jsonObjectScene = new JSONObject();
//		JSONObject jsonObjectSceneId = new JSONObject();
//		jsonObjectSceneId.put("scene_str", id);
//		jsonObjectScene.put("scene", jsonObjectSceneId);
//		jsonObject.put("action_info", jsonObjectScene);
//		logger.info("jsonObject:" + jsonObject.toString());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("serverIp", "47.106.188.61");
		jsonObject.put("userId", 10093);
		String url = "http://www.szwd.online/userInfo/getQRcode";
		JSONObject result = WxUtil.postToWxServer(url, jsonObject.toString());
		System.out.println("result:" + result);
		String data = result.getString("data");
		System.out.println("data:" + data);
		byte[] bArr = Base64.getMimeDecoder().decode(data);
//		WxUtil.byteToImage(bArr, "F:/fntTask/wxQRcode.jpg");
		String encoded = Base64.getEncoder().encodeToString(bArr);
		System.out.println("data:" + data);
		System.out.println("encoded:" + encoded);
		System.out.println("encoded.equals(data):" + encoded.equals(data));
	}

	@Test
	public void test12Clock() {
		System.out.println("12点前？，flag = " + booleanBeforeTwelveClock());
	}

	private boolean booleanBeforeTwelveClock() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();// 取当前日期。
		Calendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),12,0,0);
		System.out.println(format.format(calendar.getTime()));
		Date date = new Date();
		return date.before(calendar.getTime());
	}

	@Test
	public void testBCryptPasswordEncoder() {
		System.out.println("密码"+ new BCryptPasswordEncoder().encode("123456"));

	}

	@Test
	public void test1() {
		Customer customer = new Customer();
		String str = "customerLogo=%s";
		str = String.format(str, customer.getCustomerName());
		System.out.println(str);
	}
}
