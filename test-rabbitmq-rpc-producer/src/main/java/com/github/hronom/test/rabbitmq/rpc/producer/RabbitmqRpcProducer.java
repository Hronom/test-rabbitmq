package com.github.hronom.test.rabbitmq.rpc.producer;

import com.github.hronom.test.rabbitmq.common.utils.SerializationUtils;
import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.pojos.TokenizedTextPojo;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class RabbitmqRpcProducer implements AutoCloseable {
    private final String requestQueueName = "test_queue";

    private final String rabbitMqHostname = "localhost";
    private final int rabbitMqPort = 5672;

    private final String rabbitMqUsername = "guest";
    private final String rabbitMqPassword = "guest";

    private final Connection connection;
    private final Channel channel;
    private final String replyQueueName;
    private final QueueingConsumer consumer;

    public RabbitmqRpcProducer() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(requestQueueName, false, false, false, null);
        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public TokenizedTextPojo call(TextPojo textPojo) throws Exception {
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props =
            new AMQP
                .BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                /*.contentEncoding(StandardCharsets.UTF_8.name())
                .contentType("text/plain")*/
                .build();

        byte[] dataOut = SerializationUtils.serialize(textPojo);
        channel.basicPublish("", requestQueueName, props, dataOut);

        TokenizedTextPojo response = null;
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                Object object = SerializationUtils.deserialize(delivery.getBody());
                response = (TokenizedTextPojo) object;
                break;
            }
        }
        return response;
    }

    public String getRequestQueueName() {
        return requestQueueName;
    }
}