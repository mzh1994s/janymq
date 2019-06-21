package cn.mzhong.janymq.line;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;

import java.lang.reflect.Method;
import java.util.Objects;

public abstract class Line {
    /**
     * 流水线名称
     */
    protected String value;

    /**
     * 流水线版本号，参数级修改更新时使用，默认版本号为default
     */
    protected String version;

    /**
     * 空闲时每次检测延时时间
     *
     * @return
     */
    protected long idleInterval;

    /**
     * 每次任务完成后延时时间
     *
     * @return
     */
    protected long sleepInterval;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getIdleInterval() {
        return idleInterval;
    }

    public void setIdleInterval(long idleInterval) {
        this.idleInterval = idleInterval;
    }

    public long getSleepInterval() {
        return sleepInterval;
    }

    public void setSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    public abstract String ID();

    protected static String parameterTypeString(Method method) {
        StringBuilder parameterBuilder = new StringBuilder();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            for (Class<?> parameterType : method.getParameterTypes()) {
                parameterBuilder.append(parameterType.getName()).append(',');
            }
            parameterBuilder.deleteCharAt(parameterBuilder.length() - 1);
        }
        return parameterBuilder.toString();
    }

    protected static String lineName(Class<?> _interface, Method method) {
        return _interface.getName() + "." + method.getName() + "(" + parameterTypeString(method) + ")";
    }

    public static String ID(Class<?> _interface, Method method, Pipleline annotation) {
        String lineName = annotation.value();
        if (Objects.equals(lineName.trim(), "")) {
            lineName = lineName(_interface, method);
        }
        LineIDGenerator generator = new LineIDGenerator(lineName, "#");
        generator.append(annotation.version());
        return generator.generate();
    }

    public static String ID(Class<?> _interface, Method method, Loopline annotation) {
        String lineName = annotation.value();
        if (Objects.equals(lineName.trim(), "")) {
            lineName = lineName(_interface, method);
        }
        LineIDGenerator generator = new LineIDGenerator(annotation.value(), "#");
        generator.append(annotation.version());
        return generator.generate();
    }

}
