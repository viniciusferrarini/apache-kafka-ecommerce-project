package com.vncsferrarini.ecommerce.consumer;

public interface ServiceFactory<T> {

    ConsumerService<T> create();

}
