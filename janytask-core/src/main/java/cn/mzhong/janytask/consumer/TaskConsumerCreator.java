package cn.mzhong.janytask.consumer;

/**
 * @since 1.0.1
 */
public interface TaskConsumerCreator {

    Object createConsumer(Class<?> consumerClass);
}
