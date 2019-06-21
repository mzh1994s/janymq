package cn.mzhong.janymq.line;

import java.io.*;

public class DataSerializer {

    public byte[] serialize(Object data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(data);
        return byteArrayOutputStream.toByteArray();
    }

    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
        return inputStream.readObject();
    }
}
