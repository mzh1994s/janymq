package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;
import java.util.Map;

public interface ProducerFactory {

    void registryProducer(Class<?> _class, Map<Method, MessageDao> messageDaoMap);

    Object getObject(Class<?> _class);
}
