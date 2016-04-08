package com.github.hronom.test.rabbitmq.rapid.producer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class RabbitmqRapidProducer implements AutoCloseable {
    private final String requestQueueName = "test_queue";

    private final String rabbitMqHostname = "localhost";
    private final int rabbitMqPort = 5672;

    private final String rabbitMqUsername = "guest";
    private final String rabbitMqPassword = "guest";

    private final Connection connection;
    private final Channel channel;

    public RabbitmqRapidProducer() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public void post(String message) throws Exception {
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props =
            new AMQP
                .BasicProperties
                .Builder()
                .correlationId(corrId)
                .contentEncoding(StandardCharsets.UTF_8.name())
                .contentType("text/plain")
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes(StandardCharsets.UTF_8));
    }

    public String getRequestQueueName() {
        return requestQueueName;
    }
}