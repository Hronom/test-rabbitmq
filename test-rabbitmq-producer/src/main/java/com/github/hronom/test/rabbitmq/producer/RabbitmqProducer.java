package com.github.hronom.test.rabbitmq.producer;

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

public class RabbitmqProducer implements AutoCloseable {
    private final String requestQueueName = "test_queue";

    private final String rabbitMqHostname = "localhost";
    private final int rabbitMqPort = 5672;

    private final String rabbitMqUsername = "guest";
    private final String rabbitMqPassword = "guest";

    private final Connection connection;
    private final Channel channel;
    private final String replyQueueName;
    private final QueueingConsumer consumer;

    public RabbitmqProducer() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);
        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public Object call(String message) throws Exception {
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props =
            new AMQP
                .BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .contentEncoding(StandardCharsets.UTF_8.name())
                .contentType("text/plain")
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes(StandardCharsets.UTF_8));

        String response = null;
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody());
                break;
            }
        }
        return response;
    }

    public String getRequestQueueName() {
        return requestQueueName;
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}