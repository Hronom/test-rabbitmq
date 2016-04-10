package com.github.hronom.test.rabbitmq.rpc.producer;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.pojos.TokenizedTextPojo;

import net.moznion.random.string.RandomStringGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class TestRabbitmqRpcProducer {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        final String
            stringPattern
            = "Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!";
        final RandomStringGenerator generator = new RandomStringGenerator();

        try (RabbitmqRpcProducer rpcProducer = new RabbitmqRpcProducer()) {
            int totalCountOfSendedMessages = 0;
            long totalSendTime = 0;

            long timeOfLastUpdate = 0;
            int countOfRpcInSec = 0;

            while (true) {
                //logger.info("Generate random string...");
                TextPojo textPojo = new TextPojo();
                textPojo.text = generator.generateFromPattern(stringPattern);
                try {
                    long sendingStartTime = System.currentTimeMillis();

                    /*logger.info(
                        "Emit to \"" + rpcProducer.getRequestQueueName() + "\" message: \"" + textPojo.text +
                        "\"");*/
                    TokenizedTextPojo tokenizedTextPojo = rpcProducer.call(textPojo);
                    /*logger.info("In thread " + Thread.currentThread().getId() + " receive: \"" +
                                tokenizedTextPojo.words.toString() + "\"");*/

                    long currentTime = System.currentTimeMillis();

                    long sendTime = currentTime - sendingStartTime;

                    totalSendTime += sendTime;

                    totalCountOfSendedMessages++;
                    countOfRpcInSec++;
                    if (currentTime - timeOfLastUpdate > TimeUnit.SECONDS.toMillis(1)) {
                        logger.info("Average send time: " +
                                    (double) (totalSendTime / totalCountOfSendedMessages) + " ms.");
                        logger.info("Count of RPC's in second: " + countOfRpcInSec);

                        timeOfLastUpdate = currentTime;
                        countOfRpcInSec = 0;
                    }
                } catch (Exception exception) {
                    logger.fatal("Fail!", exception);
                }
            }
        } catch (Exception exception) {
            logger.fatal("Fail!", exception);
        }
    }
}
