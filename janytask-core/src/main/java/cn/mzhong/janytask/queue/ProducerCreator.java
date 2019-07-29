package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;
import java.util.Map;

public interface ProducerCreator {

    Object create(Class<?> _class, Map<Method, MessageDao> messageDaoMap);
}
