package com.github.hronom.test.rabbitmq.spring.consumer;

import com.github.hronom.test.rabbitmq.spring.consumer.configs.RabbitmqConfiguration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan
@Import(RabbitmqConfiguration.class)
public class TestRabbitmqSpringConsumer {
    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(TestRabbitmqSpringConsumer.class).run(args);
    }
}