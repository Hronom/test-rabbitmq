package com.github.hronom.test.rabbitmq.rapid.producer;

import net.moznion.random.string.RandomStringGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestRabbitmqRapidProducer {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws
        IOException,
        TimeoutException, InterruptedException {
        final String
            stringPattern
            = "Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!";
        final RandomStringGenerator generator = new RandomStringGenerator();

        try (RabbitmqRapidProducer rapidProducer = new RabbitmqRapidProducer()) {
            while (true) {
                logger.info("Generate random string...");
                String msg = generator.generateFromPattern(stringPattern);
                try {
                    logger.info("Emit to \"" + rapidProducer.getRequestQueueName() + "\" message: \"" + msg + "\"");
                    rapidProducer.post(msg);
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (Exception exception) {
                    logger.fatal("Fail!", exception);
                }
            }
        } catch (Exception exception) {
            logger.fatal("Fail!", exception);
        }
    }
}
