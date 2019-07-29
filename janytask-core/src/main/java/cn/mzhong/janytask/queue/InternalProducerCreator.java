package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 内部生产者实例创建者
 */
class InternalProducerCreator implements ProducerCreator {

    public Object create(Class<?> _class, Map<Method, MessageDao> messageDaoMap) {
        return ProducerFactory.newInstance(messageDaoMap, _class);
    }
}