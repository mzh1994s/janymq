package cn.mzhong.janytask.producer;

public interface ProducerCreator {

    Object createProducer(Class<?> _class);
}
