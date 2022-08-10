package com.vncsferrarini.ecommerce.consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vncsferrarini.ecommerce.Message;
import com.vncsferrarini.ecommerce.MessageAdapter;
import org.apache.kafka.common.serialization.Deserializer;

public class GsonDeserializer<T> implements Deserializer<Message> {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    @Override
    public Message deserialize(String message, byte[] bytes) {
        return gson.fromJson(new String(bytes), Message.class);
    }
}
