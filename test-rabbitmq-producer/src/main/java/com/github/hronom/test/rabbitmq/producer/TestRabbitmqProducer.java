package com.github.hronom.test.rabbitmq.producer;

import net.moznion.random.string.RandomStringGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class TestRabbitmqProducer {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        final String stringPattern =
            "Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!";
        final RandomStringGenerator generator = new RandomStringGenerator();

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ForkJoinPool executor = new ForkJoinPool(availableProcessors);

        try (RabbitmqProducer tokenizerRpc = new RabbitmqProducer()) {
            for (int i = 0; i < availableProcessors; i++) {
                executor.execute(new RecursiveAction() {
                    @Override
                    public void compute() {
                        logger.info("Generate random string...");
                        String msg = generator.generateFromPattern(stringPattern);
                        logger.info("From thread " + Thread.currentThread().getId() + " emit to \"queue1\" message: \"" + msg + "\"");
                        try {
                            Object obj = tokenizerRpc.call(msg);
                            String resultStr = (String) obj;
                            logger.info(
                                "In thread " + Thread.currentThread().getId() + " receive: \"" +
                                resultStr.toString() + "\"");
                        } catch (Exception exception) {
                            logger.fatal("Fail!", exception);
                        }
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(24, TimeUnit.DAYS);
        } catch (Exception exception) {
            logger.fatal(exception);
        }
    }
}
