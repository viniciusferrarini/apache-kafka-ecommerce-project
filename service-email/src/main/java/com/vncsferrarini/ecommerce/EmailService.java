package com.vncsferrarini.ecommerce;

import com.vncsferrarini.ecommerce.consumer.ConsumerService;
import com.vncsferrarini.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService implements ConsumerService<String> {

    public static final String TOPIC = "ECOMMERCE_SEND_EMAIL";

    public static void main(String[] args) {
        new ServiceRunner(EmailService::new).start(5);
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

    public void parse(ConsumerRecord<String, Message<String>> record) {
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
        System.out.println("Email sent");
    }
}