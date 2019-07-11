package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.tool.IDGenerator;

import java.lang.reflect.Method;

public class QueueInfo {
    protected String ID;
    protected Class<?> producerClass;
    protected Method producerMethod;
    protected Class<?> consumerClass;
    protected Method consumerMethod;
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

    public QueueInfo(
            Class<?> producerClass,
            Method producerMethod,
            Class<?> consumerClass,
            Method consumerMethod,
            String value,
            String version,
            long idleInterval,
            long sleepInterval) {
        this.producerClass = producerClass;
        this.producerMethod = producerMethod;
        this.consumerClass = consumerClass;
        this.consumerMethod = consumerMethod;
        this.value = value(producerClass, producerMethod, value);
        this.version = version;
        this.idleInterval = idleInterval;
        this.sleepInterval = sleepInterval;
        this.ID = ID(this.value, version);
    }

    public String getValue() {
        return value;
    }

    public String getVersion() {
        return version;
    }

    public long getIdleInterval() {
        return idleInterval;
    }

    public long getSleepInterval() {
        return sleepInterval;
    }

    public Class<?> getProducerClass() {
        return producerClass;
    }

    public Method getProducerMethod() {
        return producerMethod;
    }

    public Class<?> getConsumerClass() {
        return consumerClass;
    }

    public Method getConsumerMethod() {
        return consumerMethod;
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
