package com.github.hronom.test.rabbitmq.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestRabbitmqConsumer {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args)
        throws IOException, TimeoutException, InterruptedException {
        final String requestQueueName = "test_queue";

        final String rabbitMqHostname = "localhost";
        final int rabbitMqPort = 5672;

        final String rabbitMqUsername = "guest";
        final String rabbitMqPassword = "guest";

        final QueueingConsumer consumer;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //replyQueueName = channel.queueDeclare().getQueue();
        channel.queueDeclare(requestQueueName, false, false, false, null);

        consumer = new QueueingConsumer(channel);
        channel.basicConsume(requestQueueName, false, consumer);

        System.out.println(" [x] Awaiting RPC requests");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            AMQP.BasicProperties props = delivery.getProperties();
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                .correlationId(props.getCorrelationId()).build();

            String message = new String(delivery.getBody());

            System.out.println(" [.] fib(" + message + ")");
            String response = "Processed: " + message;

            channel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes());

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }
}
