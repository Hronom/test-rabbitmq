package com.github.hronom.test.rabbitmq.rpc.producer;

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
            while (true) {
                logger.info("Generate random string...");
                String msg = generator.generateFromPattern(stringPattern);
                try {
                    logger.info("Emit to \"" + rpcProducer.getRequestQueueName() + "\" message: \"" + msg + "\"");
                    Object obj = rpcProducer.call(msg);
                    String resultStr = (String) obj;
                    logger.info("In thread " + Thread.currentThread().getId() + " receive: \"" +
                                resultStr.toString() + "\"");
                } catch (Exception exception) {
                    logger.fatal("Fail!", exception);
                }
            }

            //executor.awaitTermination(24, TimeUnit.DAYS);
        } catch (Exception exception) {
            logger.fatal("Fail!", exception);
        }
    }
}
