package com.vncsferrarini.ecommerce;

import com.vncsferrarini.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static final String TOPIC_NEW_ORDER = "ECOMMERCE_NEW_ORDER";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            for (var i = 0; i < 20; i++) {
                var orderId = UUID.randomUUID().toString();
                var amount = Math.random() * 5000 + 1;
                var email = Math.random() + "@email.com";
                var order = new Order(orderId, email, new BigDecimal(amount));
                orderDispatcher.send(TOPIC_NEW_ORDER, new CorrelationId(NewOrderMain.class.getSimpleName()), email, order);
            }
        }
    }
}