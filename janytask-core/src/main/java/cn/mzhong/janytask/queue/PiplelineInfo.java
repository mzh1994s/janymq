package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;

public class PiplelineInfo extends QueueInfo {

    public PiplelineInfo(Class<?> producerClass,
                         Method producerMethod,
                         Class<?> consumerClass,
                         Method consumerMethod,
                         Pipleline annotation) {
        super(producerClass,
                producerMethod,
                consumerClass,
                consumerMethod,
                annotation.value(),
                annotation.version(),
                annotation.idleInterval(),
                annotation.sleepInterval());
    }
}
