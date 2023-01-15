
package com.example.demo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.config.ScheduleConfig;
import com.example.demo.controller.thrift.RPCNetAuthService;
import com.example.demo.controller.thrift.RPCNetAuthServiceImpl;
import com.example.demo.entity.MessageType;
import com.example.demo.service.MessageTypeService;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class DemoApplication {

//	public static String global_access_token = null;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		ScheduleConfig.initSchedule();
//		startRPCServer();
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@Bean
	public AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
		AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter = new AutoJsonRpcServiceImplExporter();
		return autoJsonRpcServiceImplExporter;
	}

	private  static void   startRPCServer()
	{
		try {
			// 设置协议工厂为 TBinaryProtocol.Factory
			TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory();
			// 关联处理器与 Hello 服务的实现
			TMultiplexedProcessor processor = new TMultiplexedProcessor();
			TServerTransport t = new TServerSocket(9090);
			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(t).processor(processor));
			processor.registerProcessor(RPCNetAuthService.class.getSimpleName(), new RPCNetAuthService.Processor<RPCNetAuthService.Iface>(new RPCNetAuthServiceImpl()));
//         TSimpleServer server = new TSimpleServer(new Args(t).processor(processor));
			System.out.println("the serveris started and is listening at 9090...");
			server.serve();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}


//	企业用户点击应用调用的接口
//	@GetMapping("/home")
//	public void home(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//		String corp_id = "ww02ef9c92029980d4";
//		String redirect_uri = "http://www.szwd.online/app";
//		String login_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
//		login_url = String.format(login_url, corp_id, redirect_uri);
//		resp.sendRedirect(login_url);
//	}

	// 企业用户点击应用后，企业微信回调的接口
//	@GetMapping("/app")
//	public void app(HttpServletRequest request, HttpServletResponse resp) throws IOException {
////		遍历请求参数
////		Enumeration paramNames = request.getParameterNames();
////		while (paramNames.hasMoreElements()) {
////			String paramName = (String) paramNames.nextElement();
////			String[] paramValues = request.getParameterValues(paramName);
////			if (paramValues.length == 1) {
////				String paramValue = paramValues[0];
////				if (paramValue.length() != 0) {
////					System.out.println("参数：" + paramName + "=" + paramValue);
////				}
////			}
////		}
//		String code = request.getParameter("code");
//		System.out.println("code:" + code);
//		String accessToken = getAccessToken();
//		String resulturl  = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s&debug=1";
//		resulturl = String.format(resulturl, accessToken, code);
//		String param=resulturl;    //请求的发送的参数
//        String user_id = null;
//        System.out.println("param:" + param);
//		try {
//			URL url = new URL(param);
//			//打开和url之间的连接
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			/**设置URLConnection的参数和普通的请求属性****start***/
//			conn.setRequestProperty("accept", "*/*");
//			conn.setRequestProperty("connection", "Keep-Alive");
//			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//			conn.setRequestProperty("content-type","application/json");
//			/**设置URLConnection的参数和普通的请求属性****end***/
//			//设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
//			//最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
//			//post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
//			conn.setDoOutput(true);
//			conn.setDoInput(true);
//			conn.setRequestMethod("GET");//GET和POST必须全大写
//			/**GET方法请求*****start*/
//			conn.connect();
//			/**GET方法请求*****end*/
//			//获取URLConnection对象对应的输入流
//			InputStream is = conn.getInputStream();
//			//构造一个字符流缓存
//			BufferedReader br = new BufferedReader(new InputStreamReader(is));
//			String str = "";
//			while ((str = br.readLine()) != null) {
//				str=new String(str.getBytes(),"UTF-8");//解决中文乱码问题
//				JSONObject parseObject = JSONArray.parseObject(str);
//				//调用腾讯接口返回回来的openid
//				user_id = (String) parseObject.get("UserId");
//				//调用腾讯接口返回回来的openid
//				System.out.println("user_id:" + user_id);
//				System.out.println("微信返回消息str:" + str);
//			}
//			//关闭流
//			is.close();
//			conn.disconnect();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if(user_id == null) {
//		    System.out.println("获取user_id失败");
//		    return;
//        }
//        sendMsgToStaff(user_id);
//	}

//	获取AccessToken
//	public static String getAccessToken() {
//		if(global_access_token != null) {
//			return global_access_token;
//		}
//		String corp_id = "ww02ef9c92029980d4";
//		String secret = "AlWI5itq80R-iBwgdYDgu-3BVUg-p7d-93u_tlWpO68";
//		String resulturl  = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
//		resulturl = String.format(resulturl, corp_id, secret);
//		String param=resulturl;    //请求的发送的参数
//		System.out.println(param);
//		String access_token = null;
//		try {
//			Map map = new HashMap();
//			URL url = new URL(param);
//			//打开和url之间的连接
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			/**设置URLConnection的参数和普通的请求属性****start***/
//			conn.setRequestProperty("accept", "*/*");
//			conn.setRequestProperty("connection", "Keep-Alive");
//			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//			conn.setRequestProperty("content-type","application/json");
//			/**设置URLConnection的参数和普通的请求属性****end***/
//			//设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
//			//最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
//			//post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
//			conn.setDoOutput(true);
//			conn.setDoInput(true);
//			conn.setRequestMethod("GET");//GET和POST必须全大写
//			/**GET方法请求*****start*/
//			conn.connect();
//			/**GET方法请求*****end*/
//			//获取URLConnection对象对应的输入流
//			InputStream is = conn.getInputStream();
//			//构造一个字符流缓存
//			BufferedReader br = new BufferedReader(new InputStreamReader(is));
//			String str = "";
//			while ((str = br.readLine()) != null) {
//				str=new String(str.getBytes(),"UTF-8");//解决中文乱码问题
//				JSONObject parseObject = JSONArray.parseObject(str);
//				//调用腾讯接口返回回来的openid
//				access_token = (String) parseObject.get("access_token");
//				//调用腾讯接口返回回来的openid
//				Integer expires_in = (Integer)  parseObject.get("expires_in");
//				map.put("access_token",access_token);
//				map.put("expires_in",expires_in);
//			}
//			//关闭流
//			is.close();
//			conn.disconnect();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		global_access_token = access_token;
//		return access_token;
//	}

//	根据userID推送消息
//	public String sendMsgToStaff(String userid) {
//		String access_token = getAccessToken();
//		String resulturl  = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s&debug=1";
//		resulturl = String.format(resulturl, access_token);
//		String param=resulturl;    //请求的发送的参数
//		System.out.println(param);
//		String agent_id = "1000021";
//		String msgtype = "text";
//		JSONObject map = new JSONObject();
//		JSONObject json = new JSONObject();
//		json.put("content", "Hello," + userid + "(测试企业微信推送消息)");
//		map.put("agentid", agent_id);
//		map.put("touser", userid);
//		map.put("msgtype", msgtype);
//		map.put("text", json);
//		System.out.println("map.toString():" + map.toString());
//		JSONObject result = postToWxServer(param, map.toString());
//		System.out.println("微信返回消息result:"+ result);
//		return null;
//	}

//    @GetMapping("/send/{userId}")
//    public void send(@PathVariable("userId") String userID) {
//		System.out.println("userID:" + userID);
//		sendMsgToStaffList(userID);
//    }

//	public String sendMsgToStaffList(String userid) {
//		String access_token = getAccessToken();
//		String resulturl  = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s&debug=1";
//		resulturl = String.format(resulturl, access_token);
//		String param=resulturl;    //请求的发送的参数
//		System.out.println(param);
//		String agent_id = "1000021";
//		String msgtype = "text";
//		JSONObject map = new JSONObject();
//		JSONObject json = new JSONObject();
//		json.put("content", "Hello," + userid + "(测试企业微信推送消息给多个成员)");
//		map.put("agentid", agent_id);
//		map.put("touser", userid);
//		map.put("msgtype", msgtype);
//		map.put("text", json);
//		System.out.println("map.toString():" + map.toString());
//		JSONObject result = postToWxServer(param, map.toString());
//		System.out.println("微信返回消息result:"+ result);
//		return null;
//	}

//	private static void useHttUrlConnectionPost(String url, String userid) {
//		String agent_id = "1000021";
//		String msgtype = "text";
//		InputStream inputStream = null;
//		HttpURLConnection mHttpUrlConnection = getHttpUrlConnection(url);
//		try {
//			Map<String, String> map = new HashMap<>();
//			map.put("agentid", agent_id);
//			map.put("touser", userid);
//			map.put("msgtype", msgtype);
//			Map<String, String> maptext = new HashMap<>();
//			maptext.put("content", "Hello World," + userid);
//			map.put("text", maptext.toString());
//			System.out.println("map.toString():" + map.toString());
//			postParams(mHttpUrlConnection.getOutputStream(), map);
//			mHttpUrlConnection.connect();
//			inputStream = mHttpUrlConnection.getInputStream();
//			int code = mHttpUrlConnection.getResponseCode();
//			System.out.println("请求状态码：" + code);
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            final StringBuffer stringBuffer = new StringBuffer();
//            String line = null;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//            System.out.println("stringBuffer:" + stringBuffer);
//
//			inputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	public static HttpURLConnection getHttpUrlConnection(String url) {
//		HttpURLConnection mHttpUrlConnection = null;
//		try {
//			URL mUrl = new URL(url);
//			mHttpUrlConnection = (HttpURLConnection) mUrl.openConnection();
//			mHttpUrlConnection.setConnectTimeout(15000);
//			mHttpUrlConnection.setReadTimeout(15000);
//			mHttpUrlConnection.setRequestMethod("POST");
//			mHttpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
//			mHttpUrlConnection.setDoInput(true);
//			mHttpUrlConnection.setDoOutput(true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return mHttpUrlConnection;
//	}

	/** 向微信服务器发送Post请求，返回JSON数据 */
//	public static JSONObject postToWxServer(String url, String params) {// ...
//		HttpClient httpClient = HttpClientBuilder.create().build();
//		// 创建POST请求
//		HttpPost httpPost = new HttpPost(url);
//		httpPost.setHeader("Accept", "application/json");
//		httpPost.setHeader("Content-Type", "application/json");
//
//		StringEntity entity = new StringEntity(params, "UTF-8");
//		httpPost.setEntity(entity);
//
//		try {
//			HttpResponse response = httpClient.execute(httpPost);
//			StatusLine status = response.getStatusLine();
//			int state = status.getStatusCode();
//			System.out.println("请求返回:" + state + "(" + url + ")");
//
//			if (state == HttpStatus.SC_OK) {
//				HttpEntity reEntity = response.getEntity();
//				String jsonString = EntityUtils.toString(reEntity);
//				System.out.println("jsonString:" + jsonString);
//				JSONObject.parseObject(jsonString);
////				return JSONObject.fromObject(jsonString);
//			}
//		} catch (Exception ex) {
//			System.out.println("向微信服务器发送Post请求出错：" + ex.getMessage());
//		}
//		return null;
//	}

//	public static void postParams(OutputStream output, Map<String, String> map) {
//		try {
//			StringBuilder mStringBuilder = new StringBuilder();
//			for (String key : map.keySet()) {
//				if (!mStringBuilder.toString().isEmpty()) {
//					mStringBuilder.append("&");
//				}
//				mStringBuilder.append(URLEncoder.encode(key, "UTF-8"));
//				mStringBuilder.append("=");
//				mStringBuilder.append(URLEncoder.encode(map.get(key), "UTF-8"));
//			}
//			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output,
//					StandardCharsets.UTF_8));
//			writer.write(mStringBuilder.toString());
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}








//	@RequestMapping(value="/{module}/{module2}/{name}")
//	public String commonController(@PathVariable String module, @PathVariable String module2, @PathVariable String name) {
//
//		return module+"/"+module2+"/"+name;
//	}
//
//
//	@RequestMapping(value="/{module}/{name}")
//	public String commonController2(@PathVariable String module, @PathVariable String name) {
//
//		return module+"/"+name;
//	}

//	@RequestMapping(value="/{name}")
//	public String commonController(@PathVariable String name) {
//
//		return name;
//	}

//	@RequestMapping(value="/{name}")
//	public void commonController(@PathVariable String name, HttpServletResponse response) throws IOException {
//
////		return name;
//		String fileName = name;
//		// 设置信息给客户端不解析
//		String type = new MimetypesFileTypeMap().getContentType(fileName);
//		// 设置contenttype，即告诉客户端所发送的数据属于什么类型
//		response.setHeader("Content-type",type);
//		// 设置编码
//		String code = new String(fileName.getBytes("utf-8"), "iso-8859-1");
//		// 设置扩展头，当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
//		response.setHeader("Content-Disposition", "attachment;filename=" + code);
//		response.setContentType("application/octet-stream;charset=ISO8859-1");
//		response.addHeader("Pargam", "no-cache");
//		response.addHeader("Cache-Control", "no-cache");
//		download(fileName, response);
//	}
//	public static void download(String filename, HttpServletResponse res) throws IOException {
//		// 发送给客户端的数据
//		// 读取filename
//		ClassPathResource classPathResource = new ClassPathResource("templates/"+filename);
//		long length = classPathResource.getFile().length();
//		res.addHeader("Content-Length",String.valueOf(length));
//		OutputStream outputStream = res.getOutputStream();
//		byte[] buff = new byte[1024];
//		BufferedInputStream bis = null;
//		InputStream inputStream =classPathResource.getInputStream();
//		bis = new BufferedInputStream(inputStream);
//		int i = bis.read(buff);
//		while (i != -1) {
//			outputStream.write(buff, 0, buff.length);
//			outputStream.flush();
//			i = bis.read(buff);
//		}
//		bis.close();
//		outputStream.close();
//	}
}
            