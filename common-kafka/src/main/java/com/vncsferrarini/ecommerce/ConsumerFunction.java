package com.vncsferrarini.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public interface ConsumerFunction<T> {

    void consume(ConsumerRecord<String, T> record);

}
