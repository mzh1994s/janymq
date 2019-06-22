package cn.mzhong.janymq.line;

import java.lang.reflect.Method;

public class PiplelineInfo extends LineInfo {

    public PiplelineInfo(Class<?> _interface, Method method, cn.mzhong.janymq.annotation.Pipleline annotation) {
        super(_interface,
                method,
                annotation.value(),
                annotation.version(),
                annotation.idleInterval(),
                annotation.sleepInterval());
    }
}
