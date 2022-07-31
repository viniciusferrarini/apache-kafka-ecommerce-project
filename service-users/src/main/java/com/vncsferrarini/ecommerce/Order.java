package com.vncsferrarini.ecommerce;

import org.apache.kafka.common.protocol.types.Field;

import java.math.BigDecimal;

public class Order {

    private final String userId, orderId, email;
    private final BigDecimal amount;

    public Order(String userId, String orderId, BigDecimal amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.email = "email@mail.com";
    }

    public String getEmail() {
        return this.email;
    }
}