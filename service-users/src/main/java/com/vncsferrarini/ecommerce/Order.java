package com.vncsferrarini.ecommerce;

import org.apache.kafka.common.protocol.types.Field;

import java.math.BigDecimal;

public class Order {

    private final String orderId, email;
    private final BigDecimal amount;

    public Order(String orderId, String email, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}