package com.github.hronom.test.rabbitmq.rpc.consumer;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.pojos.TokenizedTextPojo;
import com.github.hronom.test.rabbitmq.common.utils.SerializationUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRabbitmqRpcConsumer {
    private static final Logger logger = LogManager.getLogger();
    private static final String requestQueueName = "test_queue";

    private static final String rabbitMqHostname = "localhost";
    private static final int rabbitMqPort = 5672;

    private static final String rabbitMqUsername = "guest";
    private static final String rabbitMqPassword = "guest";
    private static final Pattern pattern = Pattern.compile("\\w+");

    public static void main(String[] args)
        throws IOException, TimeoutException, InterruptedException, ClassNotFoundException {

        final QueueingConsumer consumer;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMqHostname);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1); // Per consumer limit

        channel.queueDeclare(requestQueueName, false, false, false, null);

        consumer = new QueueingConsumer(channel);
        channel.basicConsume(requestQueueName, false, consumer);

        logger.info("[x] Awaiting RPC requests");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            AMQP.BasicProperties props = delivery.getProperties();
            TextPojo textPojo = (TextPojo) SerializationUtils.deserialize(delivery.getBody());

            //logger.info("[.] " + textPojo.text);

            TokenizedTextPojo tokenizedTextPojo = new TokenizedTextPojo();
            tokenizedTextPojo.words = new ArrayList<>();
            Matcher matcher = pattern.matcher(textPojo.text);
            while (matcher.find()) {
                tokenizedTextPojo.words.add(matcher.group());
            }
            //logger.info("Processed: " + tokenizedTextPojo.words.toString());

            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                .correlationId(props.getCorrelationId()).build();
            byte[] dataOut = SerializationUtils.serialize(tokenizedTextPojo);
            channel.basicPublish("", props.getReplyTo(), replyProps, dataOut);

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            //Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }
}
