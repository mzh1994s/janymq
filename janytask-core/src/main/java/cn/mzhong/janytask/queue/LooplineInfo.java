package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.annotation.Loopline;

import java.lang.reflect.Method;

public class LooplineInfo extends QueueInfo {

    public LooplineInfo(Class<?> _interface, Method method, Loopline annotation) {
        super(_interface,
                method,
                annotation.value(),
                annotation.version(),
                annotation.idleInterval(),
                annotation.sleepInterval());
    }
}
