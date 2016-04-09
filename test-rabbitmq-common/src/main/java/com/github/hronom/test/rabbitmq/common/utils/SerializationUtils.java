package com.github.hronom.test.rabbitmq.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializationUtils {
    private SerializationUtils() {
    }

    public static byte[] serialize(Object object) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream on = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(on);
        os.writeObject(object);
        return on.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
