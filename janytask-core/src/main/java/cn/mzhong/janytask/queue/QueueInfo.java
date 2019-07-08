package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.tool.IDGenerator;

import java.lang.reflect.Method;

public abstract class QueueInfo {
    protected String ID;
    protected Class<?> _interface;
    protected Method method;
    /**
     * 列表名称
     */
    protected String value;

    /**
     * 列表版本号，参数级修改更新时使用，默认版本号为default
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

    public QueueInfo(Class<?> _interface, Method method, String value, String version, long idleInterval, long sleepInterval) {
        this._interface = _interface;
        this.method = method;
        this.value = value(_interface, method, value);
        this.version = version;
        this.idleInterval = idleInterval;
        this.sleepInterval = sleepInterval;
        this.ID = ID(this.value, version);
    }

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

    public Class<?> getInterface() {
        return _interface;
    }

    public void setInterface(Class<?> _interface) {
        this._interface = _interface;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void value() {

    }

    public String ID() {
        return this.ID;
    }

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

    protected static String value(Class<?> _interface, Method method, String value) {
        String lineName = value;
        if ("".equals(lineName.trim())) {
            lineName = _interface.getName() + "." + method.getName() + "(" + parameterTypeString(method) + ")";
        }
        return lineName;
    }

    protected static String ID(String value, String... items) {
        IDGenerator generator = new IDGenerator(value, "#");
        for (String item : items) {
            generator.append(item);
        }
        return generator.generate();
    }
}
