package com.example.mybatplusdemo.controller.stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WebhookController {

//    @Value("${stripe.webhook.secret}")
//    private static final String stripeWebhookSecret = "sk_test_51R8e4JQJ3qB0sPiDn2YnVwPk8XxuIAI2sVOzzhGAoowY1zM45ybemrHPJPueGk2pyW8JRlqtS08F90kEEXlTHGxQ0025kPzXjZ";
//    private static final String stripeWebhookSecret = "whsec_oAP4MzYnj8FNGs7WB1zCMBhFwFp7xZy8";
    // wsc
    private static final String stripeWebhookSecret = "whsec_9gIQrPzPlFe8OmyiLNCOJ5RZp54QCxJF";

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            if (dataObjectDeserializer.getObject().isPresent()) {
                PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
                switch (event.getType()) {
                    case "payment_intent.succeeded":
                        System.out.println("Payment succeeded: " + paymentIntent.getId());
                        break;
                    case "payment_intent.payment_failed":
                        System.out.println("Payment failed: " + paymentIntent.getId());
                        break;
                    default:
                        System.out.println("Unhandled event type: " + event.getType());
                }
            }
            return new ResponseEntity<>("Webhook received successfully", HttpStatus.OK);
        } catch (SignatureVerificationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}