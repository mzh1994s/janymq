package cn.mzhong.janymq.line;

import java.lang.reflect.Method;

public class Loopline extends Line {

    protected String ID;

    public Loopline(Class<?> _interface, Method method, cn.mzhong.janymq.annotation.Loopline annotation) {
        super.value = annotation.value();
        super.version = annotation.version();
        super.sleepInterval = annotation.sleepInterval();
        super.idleInterval = annotation.idleInterval();
        this.ID = ID(_interface, method, annotation);
    }

    @Override
    public String ID() {
        return ID;
    }
}
