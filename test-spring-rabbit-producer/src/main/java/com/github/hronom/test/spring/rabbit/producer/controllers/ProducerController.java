package com.github.hronom.test.spring.rabbit.producer.controllers;

import org.apache.logging.log4j.LogManager;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Random;

@Controller
public class ProducerController {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private final Random random = new Random();

    @Autowired
    private AmqpTemplate template;

    @Scheduled(fixedDelay = 5000)
    public void doSomething() {
        logger.info("Generate random int...");
        String msg = "Test message " + random.nextInt();
        logger.info("Emit to \"queue1\" message: \"" + msg + "\"");
        Object obj = template.convertSendAndReceive("queue1", msg);
        String resultStr = (String) obj;
        logger.info("Receive: \"" + resultStr + "\"");
    }
}
