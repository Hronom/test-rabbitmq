package com.github.hronom.test.rabbitmq.rapid.producer;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import net.moznion.random.string.RandomStringGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestRabbitmqRapidProducer {
    private static final Logger logger = LogManager.getLogger();

    private static final String
        stringPattern
        = "Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!";

    private static final String requestQueueName = "test_rapid_queue";
    private static final String routingKey = "simple_message";

    private static final String rabbitMqHostname = "localhost";
    private static final int rabbitMqPort = 5672;

    private static final String rabbitMqUsername = "guest";
    private static final String rabbitMqPassword = "guest";

    private static Connection connection;
    private static Channel channel;

    public static void main(String[] args) throws
        IOException,
        TimeoutException, InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(requestQueueName, false, false, false, null);
        channel.queueBind(requestQueueName, "amq.direct", routingKey);

        final RandomStringGenerator generator = new RandomStringGenerator();

        try (RabbitmqRapidProducer rapidProducer = new RabbitmqRapidProducer()) {
            int totalCountOfSendedMessages = 0;
            long totalSendTime = 0;

            long timeOfLastUpdate = 0;
            int countOfMessagesInSec = 0;

            while (true) {
                StringBuilder sb = new StringBuilder();
                sb.append(generator.generateFromPattern(stringPattern));
                for (int i = 0; i < 100_000; i++) {
                    sb.append(i);
                }

                TextPojo textPojo = new TextPojo();
                textPojo.text = sb.toString();
                try {
                    long sendingStartTime = System.currentTimeMillis();

                    //logger.info("Emit to \"" + rapidProducer.getRequestQueueName() + "\" message: \"" + textPojo.text + "\"");
                    rapidProducer.post(textPojo);
                    //Thread.sleep(TimeUnit.SECONDS.toMillis(1));

                    long currentTime = System.currentTimeMillis();

                    long sendTime = currentTime - sendingStartTime;

                    totalSendTime += sendTime;

                    totalCountOfSendedMessages++;
                    countOfMessagesInSec++;
                    if (currentTime - timeOfLastUpdate > TimeUnit.SECONDS.toMillis(1)) {
                        logger.info("Average send time: " +
                                    (double) (totalSendTime / totalCountOfSendedMessages) + " ms.");
                        logger.info("Count of messages in second: " + countOfMessagesInSec);

                        timeOfLastUpdate = currentTime;
                        countOfMessagesInSec = 0;
                    }
                } catch (Exception exception) {
                    logger.fatal("Fail!", exception);
                }
            }
        } catch (Exception exception) {
            logger.fatal("Fail!", exception);
        }
    }
}
