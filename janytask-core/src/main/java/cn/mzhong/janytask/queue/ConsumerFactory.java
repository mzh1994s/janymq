package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContextAware;

/**
 * @since 2.0.0
 */
public interface ConsumerFactory extends TaskContextAware {

    void registryConsumer(Class<?> _class);

    Object getObject(Class<?> consumerClass);
}
