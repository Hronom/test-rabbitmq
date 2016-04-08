package com.github.hronom.test.spring.rabbit.consumer;

import com.github.hronom.test.spring.rabbit.consumer.configs.RabbitConfiguration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan
@Import(RabbitConfiguration.class)
public class TestSpringRabbitConsumer {
    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(TestSpringRabbitConsumer.class).run(args);
    }
}