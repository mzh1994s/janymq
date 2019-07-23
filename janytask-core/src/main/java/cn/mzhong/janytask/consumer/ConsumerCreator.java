package cn.mzhong.janytask.consumer;

/**
 * @since 2.0.0
 */
public interface ConsumerCreator {

    Object createConsumer(Class<?> consumerClass);
}
