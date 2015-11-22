package com.github.hronom.test.spring.rabbit.producer.controllers;

import org.apache.logging.log4j.LogManager;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class SampleController {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    @Autowired
    private AmqpTemplate template;

    @Scheduled(fixedDelay = 5000)
    public void doSomething() {
        logger.info("Emit to queue1");
        template.convertAndSend("queue1", "Test message");
    }
}
