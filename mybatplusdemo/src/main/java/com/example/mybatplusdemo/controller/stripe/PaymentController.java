package com.example.mybatplusdemo.controller.stripe;

import com.example.mybatplusdemo.service.StripeService;
import com.example.mybatplusdemo.utils.R;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.elasticsearch.core.List;
//import org.elasticsearch.core.Map;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import com.stripe.model.Coupon;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PaymentController {

//    @Value("${stripe.keys.public}")
//    private String API_PUBLIC_KEY;

//    @Value("${stripe.keys.public}")
    private String API_PUBLIC_KEY = "123456";

    private StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("/subscription")
    public String subscriptionPage(Model model) {
        model.addAttribute("stripePublicKey", API_PUBLIC_KEY);
        return "subscription";
    }

    @GetMapping("/charge")
    public String chargePage(Model model) {
        model.addAttribute("stripePublicKey", API_PUBLIC_KEY);
        return "charge";
    }

    @ResponseBody
    @GetMapping("/stripePaySussess")
    public R<Map> subscriptionPage() {
        Map<String, String> map = new HashMap<>();
        map.put("stripePaySussess", "stripePaySussess");
        return R.data(map);
    }

    /*========== REST APIs for Handling Payments ===================*/

    @PostMapping("/create-subscription")
    public @ResponseBody
    Response createSubscription(String email, String token, String plan, String coupon) {
        //validate data
        if (token == null || plan.isEmpty()) {
            return new Response(false, "Stripe payment token is missing. Please, try again later.");
        }

        //create customer first
        String customerId = stripeService.createCustomer(email, token);

        if (customerId == null) {
            return new Response(false, "An error occurred while trying to create a customer.");
        }

        //create subscription
        String subscriptionId = stripeService.createSubscription(customerId, plan, coupon);
        if (subscriptionId == null) {
            return new Response(false, "An error occurred while trying to create a subscription.");
        }

        // Ideally you should store customerId and subscriptionId along with customer object here.
        // These values are required to update or cancel the subscription at later stage.

        return new Response(true, "Success! Your subscription id is " + subscriptionId);
    }

    @PostMapping("/cancel-subscription")
    public @ResponseBody
    Response cancelSubscription(String subscriptionId) {
        boolean status = stripeService.cancelSubscription(subscriptionId);
        if (!status) {
            return new Response(false, "Failed to cancel the subscription. Please, try later.");
        }
        return new Response(true, "Subscription cancelled successfully.");
    }

    @PostMapping("/coupon-validator")
    public @ResponseBody
    Response couponValidator(String code) {
        Coupon coupon = stripeService.retrieveCoupon(code);
        if (coupon != null && coupon.getValid()) {
            String details = (coupon.getPercentOff() == null ? "$" + (coupon.getAmountOff() / 100) : coupon.getPercentOff() + "%") +
                    " OFF " + coupon.getDuration();
            return new Response(true, details);
        } else {
            return new Response(false, "This coupon code is not available. This may be because it has expired or has " +
                    "already been applied to your account.");
        }
    }

    @PostMapping("/create-charge")
    public @ResponseBody
    Response createCharge(String email, String token) {
        //validate data
        if (token == null) {
            return new Response(false, "Stripe payment token is missing. Please, try again later.");
        }

        //create charge
        String chargeId = stripeService.createCharge(email, token, 999); //$9.99 USD
        if (chargeId == null) {
            return new Response(false, "An error occurred while trying to create a charge.");
        }

        // You may want to store charge id along with order information

        return new Response(true, "Success! Your charge id is " + chargeId);
    }

    // 成功
//    @ResponseBody
//    @PostMapping("/create-checkout-session")
//    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> payload) throws StripeException {
//        Stripe.apiKey = "sk_test_51R8e4JQJ3qB0sPiDn2YnVwPk8XxuIAI2sVOzzhGAoowY1zM45ybemrHPJPueGk2pyW8JRlqtS08F90kEEXlTHGxQ0025kPzXjZ";
//
//        // 获取前端传递的金额和币种
//        long amount = ((Number) payload.getOrDefault("amount", 1000)).longValue(); // 单位：分
//        String currency = (String) payload.getOrDefault("currency", "eur");
//
//        // 回调地址
////        String successUrl = "https://your-site.com/payment-success";
//        String successUrl = "http://192.168.10.4:8090/charge";
//        String cancelUrl = "https://your-site.com/payment-cancel";
//
//        // 创建 Session 参数
//        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl(successUrl)
//                .setCancelUrl(cancelUrl)
//                .setCurrency(currency)
//                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.ALIPAY)
//                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.WECHAT_PAY);
//
//        // 添加商品信息
//        paramsBuilder.addLineItem(
//                SessionCreateParams.LineItem.builder()
//                        .setQuantity(1L)
//                        .setPriceData(
//                                SessionCreateParams.LineItem.PriceData.builder()
//                                        .setCurrency(currency)
//                                        .setUnitAmount(amount)
//                                        .setProductData(
//                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                        .setName("测试商品")
//                                                        .build())
//                                        .build())
//                        .build());
//
//        // 配置微信支付选项
//        SessionCreateParams.PaymentMethodOptions paymentMethodOptions = SessionCreateParams.PaymentMethodOptions.builder()
//                .setWechatPay(
//                        SessionCreateParams.PaymentMethodOptions.WechatPay.builder()
//                                .putExtraParam("client", "web") // 使用字符串设置客户端为 web
//                                .build()
//                )
//                .build();
//
//        // 设置支付方式选项
//        paramsBuilder.setPaymentMethodOptions(paymentMethodOptions);
//
//        // 创建 Session
//        Session session = Session.create(paramsBuilder.build());
//
//        // 返回支付链接
//        Map<String, String> result = new HashMap<>();
//        result.put("checkoutUrl", session.getUrl());
//        return result;
//    }


    @ResponseBody
    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> payload) throws StripeException {
        Stripe.apiKey = "sk_test_51R8e4JQJ3qB0sPiDn2YnVwPk8XxuIAI2sVOzzhGAoowY1zM45ybemrHPJPueGk2pyW8JRlqtS08F90kEEXlTHGxQ0025kPzXjZ";

        // Get the amount (in cents) and currency from the payload
        long amount = ((Number) payload.getOrDefault("amount", 1000)).longValue(); // Unit: cents
        String currency = (String) payload.getOrDefault("currency", "eur");

        // Define the success and cancel URLs
//        String successUrl = "https://your-site.com/payment-success";
        String successUrl = "http://192.168.10.4:8090/stripePaySussess";
        String cancelUrl = "https://your-site.com/payment-cancel";

        // Create the session parameters
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setCurrency(currency);

        // Add product/item details
        paramsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(currency)
                                        .setUnitAmount(amount)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Test Product")
                                                        .build())
                                        .build())
                        .build()
        ).putMetadata("order_id", "123456")  // ✅ 自定义元数据，传入你的订单号
                .putMetadata("user_id", "7890");

        // Add payment method types (automatic, no need to specify one-by-one)
        paramsBuilder.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.ALIPAY)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.WECHAT_PAY);

        // Set payment method options (for WeChat Pay, specify 'client' as 'web')
        SessionCreateParams.PaymentMethodOptions paymentMethodOptions = SessionCreateParams.PaymentMethodOptions.builder()
                .setWechatPay(
                        SessionCreateParams.PaymentMethodOptions.WechatPay.builder()
                                .setClient(SessionCreateParams.PaymentMethodOptions.WechatPay.Client.WEB)  // Set 'client' to 'web'
                                .build()
                )
                .build();

        // Set payment method options to session
        paramsBuilder.setPaymentMethodOptions(paymentMethodOptions);

        // Create the session
        Session session = Session.create(paramsBuilder.build());

        // Return the checkout URL
        Map<String, String> result = new HashMap<>();
        result.put("checkoutUrl", session.getUrl());
        return result;
    }

    public static PaymentIntent createPaymentIntent(int amount, String currency) throws Exception {
//        StripeConfig.init();
        Stripe.apiKey = "sk_test_51R8e4JQJ3qB0sPiDn2YnVwPk8XxuIAI2sVOzzhGAoowY1zM45ybemrHPJPueGk2pyW8JRlqtS08F90kEEXlTHGxQ0025kPzXjZ";

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) amount)
                .setCurrency(currency)
//                .addPaymentMethodType("card")
//                .addPaymentMethodType("alipay")
//                .addPaymentMethodType("wechat_pay") // 可选，需后台开启
//                .addPaymentMethodType("grabpay") // 举例
//                .addPaymentMethodType("ideal") // 举例
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    public static void main(String[] args) {
        try {
            PaymentIntent paymentIntent = createPaymentIntent(1000, "eur");
            System.out.println("Payment Intent ID: " + paymentIntent.getId());
            System.out.println(paymentIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}