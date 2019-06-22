package cn.mzhong.janymq.line;

import java.lang.reflect.Method;

public class LooplineInfo extends LineInfo {

    public LooplineInfo(Class<?> _interface, Method method, cn.mzhong.janymq.annotation.Loopline annotation) {
        super(_interface,
                method,
                annotation.value(),
                annotation.version(),
                annotation.idleInterval(),
                annotation.sleepInterval());
    }
}
