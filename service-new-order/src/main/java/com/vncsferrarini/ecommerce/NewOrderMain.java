package com.vncsferrarini.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static final String TOPIC_NEW_ORDER = "ECOMMERCE_NEW_ORDER";
    public static final String TOPIC_SEND_EMAIL = "ECOMMERCE_SEND_EMAIL";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 20; i++) {
                    var orderId = UUID.randomUUID().toString();
                    var amount = Math.random() * 5000 + 1;
                    var email = Math.random() + "@email.com";
                    var order = new Order(orderId, email, new BigDecimal(amount));
                    orderDispatcher.send(TOPIC_NEW_ORDER, new CorrelationId(NewOrderMain.class.getSimpleName()), email, order);
                    emailDispatcher.send(TOPIC_SEND_EMAIL, new CorrelationId(NewOrderMain.class.getSimpleName()), email, "Thank you for your order!");
                }
            }
        }
    }
}