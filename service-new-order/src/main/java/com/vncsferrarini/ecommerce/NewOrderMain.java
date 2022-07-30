package com.vncsferrarini.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static final String TOPIC = "ECOMMERCE_NEW_ORDER";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 20; i++) {
                    var orderId = UUID.randomUUID().toString();
                    var userId = UUID.randomUUID().toString();
                    var amount = Math.random() * 5000 + 1;
                    var order = new Order(userId, orderId, new BigDecimal(amount));
                    orderDispatcher.send(TOPIC, orderId, order);
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", orderId, "Thank you for your order!");
                }
            }
        }
    }
}