package com.example.demo.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ApprovalDetail;
import com.example.demo.entity.AuditDelayCount;
import com.example.demo.entity.PendingApproval;
import com.example.demo.entity.PendingApprovalDetail;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static Logger logger = Logger.getLogger(OkHttpUtil.class);

    /**
     * 获取待审批数据的url
     */
//    public static final String localDataServiceUrl = "http://127.0.0.1:8111";
//    http://localhost:8111
//    public static final String httpUrl = localDataServiceUrl;

    public static String get(String url, Map<String, String> queries) {
        StringBuffer stringBuffer = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator<Map.Entry<String, String>> iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                stringBuffer.append("/" + entry.getValue());
//                if (firstFlag) {
//                    stringBuffer.append("?" + entry.getKey() + "=" + entry.getValue());
//                    firstFlag = false;
//                } else {
//                    stringBuffer.append("&" + entry.getKey() + "=" + entry.getValue());
//                }
            }
        }
        Request request = new Request.Builder() //
//                .addHeader("cookie", null) //
                .url(stringBuffer.toString()) //
                .build();
        Response response = null;
        OkHttpClient okHttpClient = new OkHttpClient.Builder() //
                .connectTimeout(30, TimeUnit.SECONDS) //
                .readTimeout(30, TimeUnit.SECONDS) //
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
            int code = response.code();
            if(code == 200) {
                logger.info("请求远程接口成功，url = " + stringBuffer);
                return response.body().string();
            } else {
                logger.error("请求失败， code = " + code + ",url = " + stringBuffer);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("请求失败， url = " + stringBuffer);
        } finally {
            if(response != null) {
                response.close();
            }
        }
        return null;
    }

    // 暂时用不上
//    public static List<PendingApproval> getPendingApprovalList(String serverUrl) {
//        String responseData = OkHttpUtil.get(serverUrl + "/pendingApproval/list");
//        if(responseData == null) {
//            logger.error("responseData 为 null");
//            return null;
//        }
//        JSONObject jsonObject = JSONObject.parseObject(responseData);
//        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
//        if(jsonArrayData == null) {
//            logger.error("jsonArrayData 为null");
//            return null;
//        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
//        List<PendingApproval> pendingApprovalList = new ArrayList<>();
//        for(int i = 0; i < jsonArrayData.size(); i++) {
//            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
//            PendingApproval pendingApproval = new PendingApproval();
//            pendingApproval.setCodeid(jsonObjectPendingApproval.getString("codeid"));
//            pendingApproval.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
//            pendingApproval.setLastAuditTime(jsonObjectPendingApproval.getDate("lastAuditTime"));
//            pendingApprovalList.add(pendingApproval);
//        }
//        return pendingApprovalList;
//    }

    public static List<AuditDelayCount> getAuditDelayCountList(String serverUrl) {
        String responseData = OkHttpUtil.get(serverUrl + "/auditDelayCount/list", null);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            return null;
        }
        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<AuditDelayCount> auditDelayCountList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectAuditDelayCount = (JSONObject) jsonArrayData.get(i);
            AuditDelayCount auditDelayCount = new AuditDelayCount();
            auditDelayCount.setJobuser(jsonObjectAuditDelayCount.getString("jobuser"));
            auditDelayCount.setAdcount(jsonObjectAuditDelayCount.getInteger("adcount"));
            auditDelayCount.setDelaycount(jsonObjectAuditDelayCount.getInteger("delaycount"));
            auditDelayCountList.add(auditDelayCount);
        }
        return auditDelayCountList;
    }

    public static List<PendingApproval> getPendingApprovalList(String serverUrl) {
        String responseData = OkHttpUtil.get(serverUrl + "/pendingApproval/list", null);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            return null;
        }
        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<PendingApproval> pendingApprovalList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
            pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
            pendingApproval.setTotalCount(jsonObjectPendingApproval.getInteger("totalCount"));
            pendingApprovalList.add(pendingApproval);
        }
        return pendingApprovalList;
    }

    public static List<PendingApprovalDetail> getPendingApprovalDetailList(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/pendingApprovalDetail/listByUserCode", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            return null;
        }
        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<PendingApprovalDetail> pendingApprovalDetailList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
            PendingApprovalDetail pendingApprovalDetail = new PendingApprovalDetail();
            pendingApprovalDetail.setAuditName(jsonObjectPendingApproval.getString("auditName"));
            pendingApprovalDetail.setProjName(jsonObjectPendingApproval.getString("projName"));
            pendingApprovalDetail.setBillName(jsonObjectPendingApproval.getString("billName"));
            pendingApprovalDetail.setDelayHour(jsonObjectPendingApproval.getInteger("delayHour"));
            pendingApprovalDetailList.add(pendingApprovalDetail);
        }
        return pendingApprovalDetailList;
    }

    public static PendingApproval getAdTototalCountByUserCode(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/pendingApproval/getAdTototalCountByUserCode", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
//        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        JSONObject jsonObjectPendingApproval = jsonObject.getJSONObject("data");
        if(jsonObjectPendingApproval == null) {
            logger.error("jsonObjectPendingApproval 为null");
            return null;
        }
        PendingApproval pendingApproval = new PendingApproval();
        pendingApproval.setJobuser(jsonObjectPendingApproval.getString("userName"));
        pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
        pendingApproval.setTotalCount(jsonObjectPendingApproval.getInteger("totalCount"));
        return pendingApproval;
    }

    public static PendingApproval getAverageTimeByUserCode(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/pendingApproval/getAverageTimeByUserCode", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONObject jsonObjectPendingApproval = jsonObject.getJSONObject("data");
        if(jsonObjectPendingApproval == null) {
            logger.error("jsonObjectPendingApproval 为null");
            return null;
        }
        PendingApproval pendingApproval = new PendingApproval();
//        pendingApproval.setJobuser(jsonObjectPendingApproval.getString("userName"));
//        pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
//        pendingApproval.setTotalCount(jsonObjectPendingApproval.getInteger("totalCount"));
//        System.out.println(jsonObjectPendingApproval.getInteger("return_value"));
        pendingApproval.setReturn_value(jsonObjectPendingApproval.getInteger("return_value"));
        return pendingApproval;
    }

    public static List<PendingApproval> getTotalApprovalRank(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/pendingApproval/getTotalApprovalRank", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            return null;
        }
        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<PendingApproval> pendingApprovalList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
            // 今日审核数目为0的用户不参与排行
            int totalCount = jsonObjectPendingApproval.getInteger("totalCount");
            if(totalCount == 0) {
                continue;
            }
            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
//            pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
            pendingApproval.setTotalCount(totalCount);
            pendingApprovalList.add(pendingApproval);
        }
        return pendingApprovalList;
    }

    public static List<ApprovalDetail> getApprovalDetailList(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/pendingApproval/getAdTototalCountByUserCode", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            return null;
        }
        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<ApprovalDetail> approvalDetaillList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
            ApprovalDetail approvalDetail = new ApprovalDetail();
//            approvalDetail.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
//            pendingApprovalDetail.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
//            pendingApprovalDetail.setTotalCount(jsonObjectPendingApproval.getInteger("totalCount"));
//            ...
            approvalDetaillList.add(approvalDetail);
        }
        return approvalDetaillList;
    }
}
