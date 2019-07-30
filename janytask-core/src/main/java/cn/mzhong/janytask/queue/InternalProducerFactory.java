package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 内部生产者实例创建者
 */
class InternalProducerFactory implements ProducerFactory {
    Map<Class<?>, Object> objectMap = new HashMap<Class<?>, Object>();

    public void registryProducer(Class<?> _class, Map<Method, MessageDao> messageDaoMap) {
        objectMap.put(_class, ProducerProxyFactory.newInstance(_class, messageDaoMap));
    }

    public Object getObject(Class<?> _class) {
        Object consumer = objectMap.get(_class);
        if (consumer == null) {
            for (Map.Entry<Class<?>, Object> entry : objectMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(_class)) {
                    consumer = entry.getValue();
                }
            }
            if (consumer == null) {
                throw new RuntimeException("未找到生产者：" + _class.getName());
            }
        }
        return consumer;
    }
}