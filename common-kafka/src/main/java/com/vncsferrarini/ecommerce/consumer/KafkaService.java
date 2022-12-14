package com.vncsferrarini.ecommerce.consumer;

import com.vncsferrarini.ecommerce.Message;
import com.vncsferrarini.ecommerce.dispatcher.GsonSerializer;
import com.vncsferrarini.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {

    private static final String KAFKA_SERVER = "127.0.0.1:9092";
    private final ConsumerFunction parse;
    private final KafkaConsumer<String, Message<T>> consumer;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(groupId, parse, properties);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(groupId, parse, properties);
        this.consumer.subscribe(topic);
    }

    private KafkaService(String groupId, ConsumerFunction<T> parse, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(initializeProperties(groupId, properties));
    }

    public void run() throws ExecutionException, InterruptedException {
        try (var deadLetter = new KafkaDispatcher<>()) {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty()) {
                    System.out.println(records.count() + " records found!");
                    for (var record : records) {
                        try {
                            this.parse.consume(record);
                        } catch (Exception e) {
                            var message = record.value();
                            deadLetter.send("ECOMMERCE_DEADLETTER", message.getId(), message.getId().continueWith("DeadLetter").toString(),
                                    new GsonSerializer().serialize("", message));
                        }
                    }
                }
            }
        }
    }

    private Properties initializeProperties(String groupId, Map<String, String> extraProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        // Serialize string to bytes
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.putAll(extraProperties);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}