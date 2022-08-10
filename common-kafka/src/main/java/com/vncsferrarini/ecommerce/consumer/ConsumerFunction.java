package com.vncsferrarini.ecommerce.consumer;

import com.vncsferrarini.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface ConsumerFunction<T> {

    void consume(ConsumerRecord<String, Message<T>> record) throws ExecutionException, InterruptedException, SQLException, IOException;

}
