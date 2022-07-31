package com.vncsferrarini.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String email, orderId;
    private final BigDecimal amount;

    public Order(String orderId, String email, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public String toString() {
        return "Order{" +
                "email='" + email + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }
}