package com.example.demo.entity.wx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class CreateWxMenu {

    public static void main(String[] args) {
//        String appid = "你的公众号id";
//        String secret = "你的公众号密钥";
//        String accessTokenStr = sendGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + secret);
//        System.out.println("access_token获取结果：" + accessTokenStr);
//        JSONObject accessTokenObj = JSONObject.parseObject(accessTokenStr);
//        String access_token = accessTokenObj.getString("access_token");
        String access_token = "64_YF-kyw1rHrsVCcf-k5Q-BGY9KX7NC_X55MwWylK4Dz9IrMCtcu2F7bTNrmckoZhDBKhnzI-ybbme41bx2PisHwmaNuWF3Cd9eAluV8ZZ118bmuuWOGFxHouJL8gIQEbABABJX";
        System.out.println("access_token:" + access_token);

        JSONObject menu = new JSONObject();
        JSONArray buttonAry = new JSONArray();
        JSONArray buttonAry2 = new JSONArray();


        Map<String, String> object1 = new HashMap<>();
        object1.put("type", "view");
        object1.put("name", "消息猫绑定微信操作指引");
        object1.put("url", "https://www.yuque.com/joshua8098/ub2w94/gm0vrrg73dqzec1z?#");

        Map<String, String> object2 = new HashMap<>();
        object2.put("type", "click");
        object2.put("name", "用料历史采购价");
        object2.put("key", "V1001_Material_History");
//        object2.put("url", "https://blog.csdn.net/long_yi_1994");

        Map<String, String> object3 = new HashMap<>();
        object3.put("type", "view");
        object3.put("name", "官方网站");
        object3.put("url", "https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Creating_Custom-Defined_Menu.html");
        buttonAry.add(object1);
//        buttonAry.add(object2);
//        buttonAry2.add(object3);
        JSONObject subButton = new JSONObject();
        subButton.put("sub_button", buttonAry);
        subButton.put("name", "菜单");
        buttonAry2.add(subButton);
        menu.put("button", buttonAry2);
        System.out.println(menu);
        String res = sendPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token, menu.toString());
        System.out.println("菜单修改结果：" + res);
    }

    /**
     * @param url   发送请求的URL
     * @param param 请求参数
     * @return 所代表远程资源的响应结果
     * @description 向指定 URL 发送POST方法的请求
     * @date 2020-01-05 21:00
     */
    public static String sendPost(String url, String param) {
        System.out.println("n==============================POST请求开始==============================");
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("url:" + url);
        System.out.println("POST请求结果：" + result);
        System.out.println("==============================POST请求结束==============================n");
        return result;
    }
}
