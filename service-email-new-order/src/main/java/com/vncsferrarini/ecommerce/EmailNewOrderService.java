package com.vncsferrarini.ecommerce;

import com.vncsferrarini.ecommerce.consumer.ConsumerService;
import com.vncsferrarini.ecommerce.consumer.ServiceRunner;
import com.vncsferrarini.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    public static final String TOPIC = "ECOMMERCE_NEW_ORDER";

    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(1);
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());

        var order = record.value().getPayload();
        var emailCode = "Thank you for your order! We are processing your order!";
        CorrelationId id = record.value().getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send("ECOMMERCE_SEND_MAIL", id, order.getEmail(), emailCode);
    }
}