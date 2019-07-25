package cn.mzhong.janytask.queue;

/**
 * @since 2.0.0
 */
public interface ConsumerCreator {

    Object createConsumer(Class<?> consumerClass);
}
