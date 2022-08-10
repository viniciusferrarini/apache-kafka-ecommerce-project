package com.vncsferrarini.ecommerce.dispatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vncsferrarini.ecommerce.Message;
import com.vncsferrarini.ecommerce.MessageAdapter;
import org.apache.kafka.common.serialization.Serializer;

public class GsonSerializer<T> implements Serializer<T> {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    @Override
    public byte[] serialize(String s, T object) {
        return gson.toJson(object).getBytes();
    }

}