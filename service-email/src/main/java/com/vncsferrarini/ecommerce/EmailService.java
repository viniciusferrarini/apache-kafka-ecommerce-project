package com.vncsferrarini.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public class EmailService {

    public static final String TOPIC = "ECOMMERCE_SEND_EMAIL";

    public static void main(String[] args) {
        var emailService = new EmailService();
        try(var service = new KafkaService<>(EmailService.class.getSimpleName(), TOPIC, emailService::parse, String.class, Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("------------------");
        System.out.println("Sending email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Email send");
    }
}