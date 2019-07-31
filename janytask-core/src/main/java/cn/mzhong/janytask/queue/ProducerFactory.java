package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContextAware;

public interface ProducerFactory extends TaskContextAware {

    void registryProducer(Class<?> _class);

    Object getObject(Class<?> _class);
}
