package cn.mzhong.janymq.line;

import java.io.*;

public class JdkDataSerializer {

    public byte[] serialize(Object data) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(data);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object deserialize(byte[] data) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            return inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
