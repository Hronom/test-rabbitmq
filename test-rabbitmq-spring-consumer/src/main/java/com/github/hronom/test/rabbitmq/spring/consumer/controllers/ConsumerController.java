package com.github.hronom.test.rabbitmq.spring.consumer.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Controller;

@Controller
public class ConsumerController {
    private static final Logger logger = LogManager.getLogger();
    private final String queueName = "test_queue";

    @RabbitListener(containerFactory = "myRabbitListenerContainerFactory", queues = queueName)
    public String processQueue(String message) {
        logger.info("Received from \"" + queueName + "\" message: \"" + message + "\"");
        try {
            // Emulate work.
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        String processedMessage = message + " - processed.";
        logger
            .info("Send to \"" + queueName + "\" processed message: \"" + processedMessage + "\"");
        return processedMessage;
    }
}
