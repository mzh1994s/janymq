package cn.mzhong.janytask.queue;

public interface ProducerCreator {

    Object createProducer(Class<?> _class);
}
