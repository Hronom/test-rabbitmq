package com.github.hronom.test.simulate.rpc;

import com.github.hronom.test.rabbitmq.common.pojos.TextPojo;
import com.github.hronom.test.rabbitmq.common.pojos.TokenizedTextPojo;
import com.github.hronom.test.rabbitmq.common.utils.SerializationUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRpcProducer {
    private static final Pattern pattern = Pattern.compile("\\w+");

    public TokenizedTextPojo call(TextPojo textPojo) throws Exception {
        byte[] dataOut = SerializationUtils.serialize(textPojo);
        TokenizedTextPojo tokenizedTextPojo = new TokenizedTextPojo();
        tokenizedTextPojo.words = new ArrayList<>();
        Matcher matcher = pattern.matcher(textPojo.text);
        while (matcher.find()) {
            tokenizedTextPojo.words.add(matcher.group());
        }
        return tokenizedTextPojo;
    }
}