package com.example.mybatplusdemo.controller.stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.EventRetrieveParams;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/webhook")
public class WebhookServlet extends HttpServlet {
    private static final String endpointSecret = "sk_test_51R8e4JQJ3qB0sPiDn2YnVwPk8XxuIAI2sVOzzhGAoowY1zM45ybemrHPJPueGk2pyW8JRlqtS08F90kEEXlTHGxQ0025kPzXjZ";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String payload = getRequestBody(request);
        String sigHeader = request.getHeader("Stripe-Signature");

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
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
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SignatureVerificationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}