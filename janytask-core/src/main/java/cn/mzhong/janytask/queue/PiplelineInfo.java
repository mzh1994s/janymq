package cn.mzhong.janytask.queue;

import java.lang.reflect.Method;

public class PiplelineInfo extends QueueInfo {

    public PiplelineInfo(Class<?> _interface, Method method, Pipleline annotation) {
        super(_interface,
                method,
                annotation.value(),
                annotation.version(),
                annotation.idleInterval(),
                annotation.sleepInterval());
    }
}
