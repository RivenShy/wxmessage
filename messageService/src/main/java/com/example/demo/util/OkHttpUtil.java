package com.example.demo.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.*;
import com.example.demo.result.R;
import com.github.pagehelper.PageInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

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

    public static String getRequest(String url, Map<String, String> queries) {
        StringBuffer stringBuffer = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator<Map.Entry<String, String>> iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
//                stringBuffer.append("/" + entry.getValue());
                if (firstFlag) {
                    stringBuffer.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    stringBuffer.append("&" + entry.getKey() + "=" + entry.getValue());
                }
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
            logger.info("responseData:" + responseData);
            return null;
        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
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

    public static AuditDelayCount getAuditDelayCountByUserCode(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/auditDelayCount/getByUserCode", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONObject jsonObjectPendingApproval = jsonObject.getJSONObject("data");
//        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonObjectPendingApproval == null) {
            logger.error("jsonObjectPendingApproval 为null");
            logger.info("responseData:" + responseData);
            return null;
        }
        AuditDelayCount auditDelayCount = new AuditDelayCount();
        auditDelayCount.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
        auditDelayCount.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
        auditDelayCount.setDelaycount(jsonObjectPendingApproval.getInteger("delaycount"));
        return auditDelayCount;
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
            logger.info("responseData:" + responseData);
            return null;
        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<PendingApproval> pendingApprovalList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
            pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
            pendingApproval.setTodayCount(jsonObjectPendingApproval.getInteger("todayCount"));
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
            logger.info("responseData:" + responseData);
            return null;
        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<PendingApprovalDetail> pendingApprovalDetailList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
            PendingApprovalDetail pendingApprovalDetail = new PendingApprovalDetail();
            pendingApprovalDetail.setAuditName(jsonObjectPendingApproval.getString("auditName"));
            pendingApprovalDetail.setProjName(jsonObjectPendingApproval.getString("projName"));
            pendingApprovalDetail.setBillName(jsonObjectPendingApproval.getString("billName"));
            pendingApprovalDetail.setDelayHour(jsonObjectPendingApproval.getFloatValue("delayHour"));
            pendingApprovalDetail.setLastAuditTime(jsonObjectPendingApproval.getDate("lastAuditTime"));
            pendingApprovalDetailList.add(pendingApprovalDetail);
        }
        return pendingApprovalDetailList;
    }

    public static List<ApprovalResult> getApprovalResultList(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/approvalResult/list", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            logger.info("responseData:" + responseData);
            return null;
        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<ApprovalResult> approvalResultList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectApprovalResult = (JSONObject) jsonArrayData.get(i);
            ApprovalResult approvalResult = new ApprovalResult();
            approvalResult.setCodeid(jsonObjectApprovalResult.getString("codeid"));
            approvalResult.setSubMitUser(jsonObjectApprovalResult.getString("subMitUser"));
            approvalResult.setSubTime(jsonObjectApprovalResult.getDate("subTime"));
            approvalResult.setStatus(jsonObjectApprovalResult.getString("status"));
            approvalResult.setAuditName(jsonObjectApprovalResult.getString("auditName"));
            approvalResult.setAuditTime(jsonObjectApprovalResult.getDate("auditTime"));
            approvalResult.setCodeDesc(jsonObjectApprovalResult.getString("codeDesc"));
            approvalResult.setNumDesc(jsonObjectApprovalResult.getString("numDesc"));
            approvalResultList.add(approvalResult);
        }
        return approvalResultList;
    }

    public static WarehouseNotice getWarehouseNoticeByDocEntry(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/warehouseNotice/getWarehouseNoticeByDocEntry", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONObject jsonObjectWarehouseNotice = jsonObject.getJSONObject("data");
        if(jsonObjectWarehouseNotice == null) {
            logger.error("jsonObjectWarehouseNotice 为null");
            logger.info("responseData:" + responseData);
            return null;
        }
        WarehouseNotice warehouseNotice = new WarehouseNotice();
        warehouseNotice.setProjName(jsonObjectWarehouseNotice.getString("projName"));
        warehouseNotice.setDocDate(jsonObjectWarehouseNotice.getDate("docDate"));
        warehouseNotice.setShopperName(jsonObjectWarehouseNotice.getString("shopperName"));
        warehouseNotice.setVedName(jsonObjectWarehouseNotice.getString("vedName"));
        warehouseNotice.setStorePerId(jsonObjectWarehouseNotice.getString("storePerId"));
        warehouseNotice.setStorePer(jsonObjectWarehouseNotice.getString("storePer"));
        return warehouseNotice;
    }

    public static List<ApprovalTimeout> getApprovalTimeout(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/approvalTimeout/list", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            logger.info("responseData:" + responseData);
            return null;
        }
        List<ApprovalTimeout> approvalTimeoutList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectApprovalTimeout = (JSONObject) jsonArrayData.get(i);
            ApprovalTimeout approvalTimeout = new ApprovalTimeout();
            approvalTimeout.setCodeid(jsonObjectApprovalTimeout.getString("codeid"));
            approvalTimeout.setSubTime(jsonObjectApprovalTimeout.getDate("subTime"));
            approvalTimeout.setAuditName(jsonObjectApprovalTimeout.getString("auditName"));
            approvalTimeout.setLastAuditTime(jsonObjectApprovalTimeout.getDate("lastAuditTime"));
            approvalTimeout.setNumDesc(jsonObjectApprovalTimeout.getString("numDesc"));
            approvalTimeout.setJobuser(jsonObjectApprovalTimeout.getString("jobuser"));
            approvalTimeout.setCodeDesc(jsonObjectApprovalTimeout.getString("codeDesc"));
            approvalTimeoutList.add(approvalTimeout);
        }
        return approvalTimeoutList;
    }

    public static List<WarehouseNoticeDetail> getWarehouseNoticeDetailByDocEntry(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/warehouseNoticeDetail/getWarehouseNoticeDetailByDocEntry", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            logger.info("responseData:" + responseData);
            return null;
        }
        List<WarehouseNoticeDetail> warehouseNoticeDetailList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectWarehouseNoticeDetail = (JSONObject) jsonArrayData.get(i);
            WarehouseNoticeDetail warehouseNoticeDetail = new WarehouseNoticeDetail();
            warehouseNoticeDetail.setItemName(jsonObjectWarehouseNoticeDetail.getString("itemName"));
            warehouseNoticeDetail.setBandName(jsonObjectWarehouseNoticeDetail.getString("bandName"));
            warehouseNoticeDetail.setModelNum(jsonObjectWarehouseNoticeDetail.getString("modelNum"));
            warehouseNoticeDetail.setUnit(jsonObjectWarehouseNoticeDetail.getString("unit"));
            warehouseNoticeDetail.setQty(jsonObjectWarehouseNoticeDetail.getInteger("qty"));
            warehouseNoticeDetail.setTechPramas(jsonObjectWarehouseNoticeDetail.getString("techPramas"));
            warehouseNoticeDetailList.add(warehouseNoticeDetail);
        }
        return warehouseNoticeDetailList;
    }

    public static List<ApprovalResultDetail> getApprovalResultDetailByCodeID(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.get(serverUrl + "/approvalResultDetail/getByCodeId", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("data");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            logger.info("responseData:" + responseData);
            return null;
        }
        List<ApprovalResultDetail> approvalResultDetailList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectApprovalResultDetail = (JSONObject) jsonArrayData.get(i);
            ApprovalResultDetail approvalResultDetail = new ApprovalResultDetail();
            approvalResultDetail.setStageName(jsonObjectApprovalResultDetail.getString("stageName"));
            approvalResultDetail.setUserName(jsonObjectApprovalResultDetail.getString("userName"));
            approvalResultDetail.setAuditMemo(jsonObjectApprovalResultDetail.getString("auditMemo"));
            approvalResultDetail.setAuditTime(jsonObjectApprovalResultDetail.getDate("auditTime"));
            approvalResultDetailList.add(approvalResultDetail);
        }
        return approvalResultDetailList;
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
            logger.info("responseData:" + responseData);
            return null;
        }
        PendingApproval pendingApproval = new PendingApproval();
        pendingApproval.setJobuser(jsonObjectPendingApproval.getString("userName"));
        pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
        pendingApproval.setTodayCount(jsonObjectPendingApproval.getInteger("todayCount"));
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
            logger.info("responseData:" + responseData);
            return null;
        }
        PendingApproval pendingApproval = new PendingApproval();
//        pendingApproval.setJobuser(jsonObjectPendingApproval.getString("userName"));
//        pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
//        pendingApproval.setTotalCount(jsonObjectPendingApproval.getInteger("totalCount"));
//        System.out.println(jsonObjectPendingApproval.getInteger("return_value"));
        pendingApproval.setReturn_value(jsonObjectPendingApproval.getFloatValue("return_value"));
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
            logger.info("responseData:" + responseData);
            return null;
        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<PendingApproval> pendingApprovalList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectPendingApproval = (JSONObject) jsonArrayData.get(i);
            // 今日审核数目为0的用户不参与排行
            int todayCount = jsonObjectPendingApproval.getInteger("todayCount");
            if(todayCount == 0) {
                continue;
            }
            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setJobuser(jsonObjectPendingApproval.getString("jobuser"));
//            pendingApproval.setAdcount(jsonObjectPendingApproval.getInteger("adcount"));
            pendingApproval.setTodayCount(todayCount);
            pendingApproval.setJobuserName(jsonObjectPendingApproval.getString("jobuserName"));
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
            logger.info("responseData:" + responseData);
            return null;
        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
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

    // todo
    public static R getMeterialHistoryPriceByCondition(String serverUrl, Map<String, String> params) {
        String responseData = OkHttpUtil.getRequest(serverUrl + "/materialHistory/getMeterialHistoryPriceByCondition", params);
        if(responseData == null) {
            logger.error("responseData 为 null");
//            return null;
            return R.fail("查询物料的历史采购价格失败");
        }
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        R result = new R();
        Integer code = jsonObject.getInteger("code");
        Boolean success = jsonObject.getBoolean("success");
        String msg = jsonObject.getString("msg");
        result.setCode(code);
        result.setSuccess(success);
        result.setMsg(msg);
        JSONObject jsonObject2 = JSONObject.parseObject(jsonObject.getString("data"));
        PageInfo pageInfo = new PageInfo();
        int pageNum = jsonObject2.getInteger("pageNum");
        int pageSize = jsonObject2.getInteger("pageSize");
        int size = jsonObject2.getInteger("size");
        String orderBy = jsonObject2.getString("orderBy");
        int startRow = jsonObject2.getInteger("startRow");
        int endRow = jsonObject2.getInteger("endRow");
        int total = jsonObject2.getInteger("total");
        int pages = jsonObject2.getInteger("pages");
        int firstPage = jsonObject2.getInteger("firstPage");
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setSize(size);
        pageInfo.setOrderBy(orderBy);
        pageInfo.setStartRow(startRow);
        pageInfo.setEndRow(endRow);
        pageInfo.setTotal(total);
        pageInfo.setPages(pages);
        pageInfo.setFirstPage(firstPage);
        int prePage = jsonObject2.getInteger("prePage");
        int nextPage = jsonObject2.getInteger("nextPage");
        int lastPage = jsonObject2.getInteger("lastPage");
        boolean isFirstPage = jsonObject2.getBoolean("isFirstPage");
        boolean isLastPage = jsonObject2.getBoolean("isLastPage");
        boolean hasPreviousPage = jsonObject2.getBoolean("hasPreviousPage");
        boolean hasNextPage = jsonObject2.getBoolean("hasNextPage");
        int navigatePages = jsonObject2.getInteger("navigatePages");
//        String navigatepageNums = jsonObject2.get("navigatepageNums");
        pageInfo.setPrePage(prePage);
        pageInfo.setNextPage(nextPage);
        pageInfo.setLastPage(lastPage);
        pageInfo.setIsFirstPage(isFirstPage);
        pageInfo.setIsFirstPage(isLastPage);
        pageInfo.setHasPreviousPage(hasPreviousPage);
        pageInfo.setHasNextPage(hasNextPage);
        pageInfo.setNavigatePages(navigatePages);
        JSONArray jsonArrayData = jsonObject2.getJSONArray("list");
        if(jsonArrayData == null) {
            logger.error("jsonArrayData 为null");
            logger.info("responseData:" + responseData);
//            return null;
            return R.fail("查询物料的历史采购价格失败");
        }
//        System.out.println("jsonArrayData.size():" + jsonArrayData.size());
        List<MaterialHistory> materialHistoryList = new ArrayList<>();
        for(int i = 0; i < jsonArrayData.size(); i++) {
            JSONObject jsonObjectAuditDelayCount = (JSONObject) jsonArrayData.get(i);
            MaterialHistory materialHistory = new MaterialHistory();
            materialHistory.setItemCode(jsonObjectAuditDelayCount.getString("itemCode"));
            materialHistory.setItemName(jsonObjectAuditDelayCount.getString("itemName"));
            materialHistory.setBandName(jsonObjectAuditDelayCount.getString("bandName"));
            materialHistory.setModelNum(jsonObjectAuditDelayCount.getString("modelNum"));
            materialHistory.setUnit(jsonObjectAuditDelayCount.getString("unit"));
            materialHistory.setMinPrice(jsonObjectAuditDelayCount.getBigDecimal("minPrice"));
            materialHistory.setMaxPrice(jsonObjectAuditDelayCount.getBigDecimal("maxPrice"));
            materialHistoryList.add(materialHistory);
        }
        pageInfo.setList(materialHistoryList);
        return R.data(pageInfo);
    }
}
