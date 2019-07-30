package cn.mzhong.janytask.queue;

/**
 * @since 2.0.0
 */
public interface ConsumerFactory {

    void registryConsumer(Class<?> _class);

    Object getObject(Class<?> consumerClass);
}
