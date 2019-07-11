package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;

public class LooplineInfo extends QueueInfo {

    public LooplineInfo(Class<?> producerClass,
                        Method producerMethod,
                        Class<?> consumerClass,
                        Method consumerMethod,
                        Loopline annotation) {
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
