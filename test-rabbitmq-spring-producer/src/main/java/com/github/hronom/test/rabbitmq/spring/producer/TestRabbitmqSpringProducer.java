package com.github.hronom.test.rabbitmq.spring.producer;

import com.github.hronom.test.rabbitmq.spring.producer.configs.AppConfig;
import com.github.hronom.test.rabbitmq.spring.producer.configs.RabbitmqConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan
@Import(value = {AppConfig.class, RabbitmqConfiguration.class})
public class TestRabbitmqSpringProducer {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestRabbitmqSpringProducer.class, args);
    }
}