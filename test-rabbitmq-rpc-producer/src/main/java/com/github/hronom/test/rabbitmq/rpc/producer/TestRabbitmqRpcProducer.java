package com.github.hronom.test.rabbitmq.rpc.producer;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.pojos.TokenizedTextPojo;

import net.moznion.random.string.RandomStringGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRabbitmqRpcProducer {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        final String
            stringPattern
            = "Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!";
        final RandomStringGenerator generator = new RandomStringGenerator();

        try (RabbitmqRpcProducer rpcProducer = new RabbitmqRpcProducer()) {
            int countOfSendedMessages = 0;
            long totalSendTime = 0;

            long timeOfLastUpdate = 0;
            while (true) {
                long sendingStartTime = System.currentTimeMillis();

                logger.info("Generate random string...");
                TextPojo textPojo = new TextPojo();
                textPojo.text = generator.generateFromPattern(stringPattern);
                try {
                    logger.info(
                        "Emit to \"" + rpcProducer.getRequestQueueName() + "\" message: \"" + textPojo.text +
                        "\"");
                    TokenizedTextPojo tokenizedTextPojo = rpcProducer.call(textPojo);
                    logger.info("In thread " + Thread.currentThread().getId() + " receive: \"" +
                                tokenizedTextPojo.words.toString() + "\"");
                } catch (Exception exception) {
                    logger.fatal("Fail!", exception);
                }

                long currentTime = System.currentTimeMillis();

                long sendTime = currentTime - sendingStartTime;
                totalSendTime += sendTime;

                countOfSendedMessages++;
                if (currentTime - timeOfLastUpdate > 1000) {
                    timeOfLastUpdate = currentTime;
                    System.out.println("Average send time: " + (totalSendTime / countOfSendedMessages) + " ms.");
                }
            }
        } catch (Exception exception) {
            logger.fatal("Fail!", exception);
        }
    }
}
