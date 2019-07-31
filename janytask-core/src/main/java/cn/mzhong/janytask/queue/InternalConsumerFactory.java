package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContext;

import java.util.HashMap;
import java.util.Map;

public class InternalConsumerFactory implements ConsumerFactory {

    Map<Class<?>, Object> objectMap = new HashMap<Class<?>, Object>();

    public void setContext(TaskContext context) {
        // do nothing
    }

    public void registryConsumer(Class<?> _class) {
        try {
            objectMap.put(_class, _class.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject(Class<?> _class) {
        Object producer = objectMap.get(_class);
        if (producer == null) {
            for (Map.Entry<Class<?>, Object> entry : objectMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(_class)) {
                    producer = entry.getValue();
                }
            }
            if (producer == null) {
                throw new RuntimeException("未找到消费者：" + _class.getName());
            }
        }
        return producer;
    }
}
