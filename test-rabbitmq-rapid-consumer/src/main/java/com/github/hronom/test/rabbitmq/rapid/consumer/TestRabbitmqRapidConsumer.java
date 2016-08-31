package com.github.hronom.test.rabbitmq.rapid.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;

public class TestRabbitmqRapidConsumer {
    private static final Logger logger = LogManager.getLogger();

    private static final String requestQueueName = "test_rapid_queue";
    private static final String routingKey = "simple_message";

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
        channel.basicQos(256); // Per consumer limit

        channel.queueDeclare(requestQueueName, false, false, false, null);
        channel.queueBind(requestQueueName, "amq.direct", routingKey);

        final LongAccumulator
            totalCountOfSendedMessages
            = new LongAccumulator(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return left + right;
            }
        }, 0);

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleCancelOk(String consumerTag) {
                System.out.println("handleCancelOk");
            }

            @Override
            public void handleCancel(String consumerTag) throws IOException {
                System.out.println("handleCancel");
            }

            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                System.out.println("handleShutdownSignal");
            }

            @Override
            public void handleRecoverOk(String consumerTag) {
                System.out.println("handleRecoverOk");
            }

            @Override
            public void handleDelivery(
                String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body
            ) throws IOException {
                try {
                    //TextPojo textPojo = (TextPojo) SerializationUtils.deserialize(delivery.getBody());
                    //logger.info("[.] " + textPojo.text);
                    //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    Thread.sleep(TimeUnit.MILLISECONDS.toMillis(300));

                    totalCountOfSendedMessages.accumulate(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(requestQueueName, true, consumer);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Count of consumed messages: " + totalCountOfSendedMessages.getThenReset());
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    }
}
