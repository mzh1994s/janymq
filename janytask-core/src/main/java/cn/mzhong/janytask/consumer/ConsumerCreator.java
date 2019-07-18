package cn.mzhong.janytask.consumer;

/**
 * @since 1.0.1
 */
public interface ConsumerCreator {

    Object createConsumer(Class<?> consumerClass);
}
