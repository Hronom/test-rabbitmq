package com.github.hronom.test.spring.rabbit.producer;

import com.github.hronom.test.spring.rabbit.producer.configs.AppConfig;
import com.github.hronom.test.spring.rabbit.producer.configs.RabbitConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan
@Import(value = {AppConfig.class, RabbitConfiguration.class})
public class TestSpringRabbitProducer {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestSpringRabbitProducer.class, args);
    }
}