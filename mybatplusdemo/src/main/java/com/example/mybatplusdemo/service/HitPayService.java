package com.example.mybatplusdemo.service;

import java.io.IOException;
import java.util.Map;

public interface HitPayService {
    String initiatePayment(String paymentDetails) throws IOException;


    Map<String, Object> createPayment(double amount, String currency, String reference);
}