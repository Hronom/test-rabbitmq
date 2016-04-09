package com.github.hronom.test.rabbitmq.rapid.consumer;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.utils.SerializationUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestRabbitmqRapidConsumer {
    private static final Logger logger = LogManager.getLogger();

    private static final String requestQueueName = "test_queue";

    private static final String rabbitMqHostname = "localhost";
    private static final int rabbitMqPort = 5672;

    private static final String rabbitMqUsername = "guest";
    private static final String rabbitMqPassword = "guest";

    public static void main(String[] args)
        throws IOException, TimeoutException, InterruptedException, ClassNotFoundException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1); // Per consumer limit

        //replyQueueName = channel.queueDeclare().getQueue();
        channel.queueDeclare(requestQueueName, false, false, false, null);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(requestQueueName, false, consumer);

        System.out.println("[x] Awaiting requests");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            TextPojo textPojo = (TextPojo) SerializationUtils.deserialize(delivery.getBody());
            System.out.println("[.] " + textPojo.text);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            Thread.sleep(TimeUnit.SECONDS.toMillis(3));
        }
    }
}
