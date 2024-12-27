package com.example.mybatplusdemo.controller;

import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.service.HitPayService;
import com.example.mybatplusdemo.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/hitPay")
public class HitPayController {

    @Resource
    private HitPayService hitPayService;

    @PostMapping("/createPayment")
    public R<Boolean> createPayment() throws IOException {
        hitPayService.createPayment(1, "SGD", "REF123");
        return R.success("操作成功！");
    }

    @PostMapping("/initiate-payment")
    public ResponseEntity<String> initiatePayment(@RequestBody String paymentDetails) {
        try {
            String paymentResponse = hitPayService.initiatePayment(paymentDetails);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("支付初始化失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
