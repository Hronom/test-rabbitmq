package com.github.hronom.test.rabbitmq.rapid.producer;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.utils.SerializationUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import net.moznion.random.string.RandomStringGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.LongBinaryOperator;

public class TestRabbitmqRapidProducer {
    private static final Logger logger = LogManager.getLogger();

    private static final String
        stringPattern
        = "Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!";

    private static final RandomStringGenerator generator = new RandomStringGenerator();

    private static final String requestQueueName = "test_rapid_queue";
    private static final String routingKey = "simple_message";

    private static final String rabbitMqHostname = "localhost";
    private static final int rabbitMqPort = 5672;

    private static final String rabbitMqUsername = "guest";
    private static final String rabbitMqPassword = "guest";

    private static Connection connection;
    private static Channel channel;

    public static void main(String[] args)
        throws IOException, TimeoutException, InterruptedException {
        AtomicBoolean run = new AtomicBoolean(true);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                run.set(false);
            }
        });

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);
        connection = factory.newConnection();
        channel = connection.createChannel();
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

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Count of consumed messages: " + totalCountOfSendedMessages.getThenReset());
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));

        while (run.get()) {
            StringBuilder sb = new StringBuilder();
            sb.append(generator.generateFromPattern(stringPattern));
            for (int i = 0; i < 100_000; i++) {
                sb.append(i);
            }

            TextPojo textPojo = new TextPojo();
            textPojo.text = sb.toString();
            try {
                long sendingStartTime = System.currentTimeMillis();

                AMQP.BasicProperties props =
                    new AMQP
                        .BasicProperties
                        .Builder()
                        .correlationId(UUID.randomUUID().toString())
                        .build();

                channel.basicPublish("amq.direct", routingKey, props, SerializationUtils.serialize(textPojo));
                totalCountOfSendedMessages.accumulate(1);
            } catch (Exception exception) {
                logger.fatal("Fail!", exception);
            }
        }

        connection.close();
    }
}
