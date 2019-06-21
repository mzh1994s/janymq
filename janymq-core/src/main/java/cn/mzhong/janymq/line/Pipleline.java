package cn.mzhong.janymq.line;

import java.lang.reflect.Method;

public class Pipleline extends Line {

    protected String ID;

    public Pipleline(Class<?> _interface, Method method, cn.mzhong.janymq.annotation.Pipleline annotation) {
        super.value = annotation.value();
        super.version = annotation.version();
        super.sleepInterval = annotation.sleepInterval();
        super.idleInterval = annotation.idleInterval();
        this.ID = ID(_interface, method, annotation);
    }

    @Override
    public String ID(){
        return ID;
    }
}
