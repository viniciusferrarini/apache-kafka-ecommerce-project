package com.vncsferrarini.ecommerce;

import com.vncsferrarini.ecommerce.consumer.KafkaService;
import com.vncsferrarini.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    public static final String TOPIC = "ECOMMERCE_NEW_ORDER";
    public static final String TOPIC_ORDER_REJECTED = "ECOMMERCE_ORDER_REJECTED";
    public static final String TOPIC_ORDER_APPROVED = "ECOMMERCE_ORDER_APPROVED";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var fraudService = new FraudDetectorService();
        try(var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(), TOPIC, fraudService::parse, Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var message = record.value();
        var order = message.getPayload();
        if (isFraud(order)) {
            System.out.println("Order is a fraud!!" + order);
            orderDispatcher.send(TOPIC_ORDER_REJECTED, message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order.getEmail(), order);
        } else {
            System.out.println("Order approved!!");
            orderDispatcher.send(TOPIC_ORDER_APPROVED, message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order.getEmail(), order);
        }
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}