package com.example.mybatplusdemo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 */
@Data
public class StuOrderBo {

    /**
     * 订单id
     */
    private Long id;

    /**
     * 设备信息
     */
    private String deviceInfo;
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 三方订单号
     */
    private String thirdOrderNo;

    /**
     * 交易类型
     */
    private String transactionType;

    /**
     * 付款银行
     */
    private String paymentBank;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 支付完成时间
     */
    private Date paymentCompletionTime;

    /**
     * 支付类型  0微信/1支付宝
     */
    private String paymentType;

    /**
     * 0未退款/1已退款
     */
    private String isRefund;

    /**
     * 学生用户id
     */
    private Long stuUserId;
    /**
     * openId
     */
    private String openId;

    /**
     * 微信支付退款单号
     */
    private String refundId;

    /**
     * 退款单号
     */
    private String outRefundNo;

}
