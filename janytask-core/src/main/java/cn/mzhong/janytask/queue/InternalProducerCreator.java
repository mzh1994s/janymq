package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 内部生产者实例创建者
 */
class InternalProducerCreator implements InstanceCreator<Object> {

    Map<Method, MessageDao> messageDaoMap;

    public InternalProducerCreator(Map<Method, MessageDao> messageDaoMap) {
        this.messageDaoMap = messageDaoMap;
    }

    public Object create(Class<Object> _class) {
        return ProducerFactory.newInstance(messageDaoMap, _class);
    }
}