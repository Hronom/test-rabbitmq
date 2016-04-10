package com.github.hronom.test.simulate.rpc;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.pojos.TokenizedTextPojo;

import net.moznion.random.string.RandomStringGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public final class TestSimulateRpc {
    private static final Logger logger = LogManager.getLogger();

    private static final String
        stringPattern
        = "Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!Ccn!CCccCn!cccccccCn!!Ccccc!cc!ccccc!cccc!!Cc!C!C!";
    private static final RandomStringGenerator generator = new RandomStringGenerator();

    public static void main(String[] args) throws Exception {
        int totalCountOfSendedMessages = 0;
        long totalSendTime = 0;

        long timeOfLastUpdate = 0;
        int countOfRpcInSec = 0;

        while(true) {
            TextPojo textPojo = new TextPojo();
            textPojo.text = generator.generateFromPattern(stringPattern);

            TestRpcProducer testRpcProducer = new TestRpcProducer();
            long sendingStartTime = System.currentTimeMillis();
            TokenizedTextPojo tokenizedTextPojo = testRpcProducer.call(textPojo);
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
        }
    }
}
