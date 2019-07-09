package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TaskProducerInitializer implements TaskComponentInitializer {

    final static Logger Log = LoggerFactory.getLogger(TaskProducerInitializer.class);

    public void init(TaskContext context) {
        Map<Class<?>, Object> producers = new HashMap<Class<?>, Object>();
        try {
            for (Class<?> producerClass : context.getProducerClassSet()) {
                Object producer = TaskProducerFactory.newInstance(context, producerClass);
                producers.put(producerClass, producer);
            }
        } catch (Exception e) {
            Log.error("提供者初始化异常", e);
        }
        context.setProducerMap(producers);
    }
}
