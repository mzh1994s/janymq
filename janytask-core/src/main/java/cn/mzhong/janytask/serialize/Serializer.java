package cn.mzhong.janytask.serialize;

public interface Serializer {

    public byte[] serialize(Object data);

    public Object deserialize(byte[] data);

}
