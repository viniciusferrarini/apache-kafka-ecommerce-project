package com.vncsferrarini.ecommerce;

import com.vncsferrarini.ecommerce.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    public CreateUserService() throws SQLException {
        var url = "jdbc:sqlite:users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users(uuid varchar(200) primary key, email varchar(200))");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String TOPIC = "ECOMMERCE_NEW_ORDER";

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var userService = new CreateUserService();
        try(var service = new KafkaService<>(CreateUserService.class.getSimpleName(), TOPIC, userService::parse, Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.key());
        System.out.println(record.value());
        var message = record.value();
        var order = message.getPayload();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    private void insertNewUser(String email) throws SQLException {
        var insert = connection.prepareStatement("insert into Users (uuid, email) values (?, ?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("User uuid and email " + email + " created!");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }
}