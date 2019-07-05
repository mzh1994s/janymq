package cn.mzhong.janymq.initializer;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.producer.MQProducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MQProducerInitializer implements MQComponentInitializer {

    final static Logger Log = LoggerFactory.getLogger(MQProducerInitializer.class);

    public void init(MQContext context) {
        Map<Class<?>, Object> producers = new HashMap<Class<?>, Object>();
        try {
            for (Class<?> producerClass : context.getProducerClassSet()) {
                Object producer = MQProducerFactory.newInstance(context, producerClass);
                producers.put(producerClass, producer);
            }
        } catch (Exception e) {
            Log.error("提供者初始化异常", e);
        }
        context.setProducerMap(producers);
    }
}
