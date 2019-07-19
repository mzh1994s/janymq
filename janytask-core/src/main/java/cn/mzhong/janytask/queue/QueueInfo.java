package cn.mzhong.janytask.queue;


import cn.mzhong.janytask.org.springframework.CronSequenceGenerator;
import cn.mzhong.janytask.tool.IDGenerator;
import cn.mzhong.janytask.util.AnnotationUtils;
import cn.mzhong.janytask.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class QueueInfo<A extends Annotation> {
    protected String ID;
    protected A annotation;
    protected MessageDao messageDao;
    protected Class<?> producerClass;
    protected Method producerMethod;
    protected Object consumer;
    protected Class<?> consumerClass;
    protected Method consumerMethod;
    CronSequenceGenerator cronSequenceGenerator;

    public QueueInfo(A annotation, Class<?> producerClass, Method producerMethod, Object consumer, Class<?> consumerClass, Method consumerMethod) {
        this.annotation = annotation;
        this.producerClass = producerClass;
        this.producerMethod = producerMethod;
        this.consumer = consumer;
        this.consumerClass = consumerClass;
        this.consumerMethod = consumerMethod;
        String value = AnnotationUtils.getAnnotationValue(annotation, "value");
        String version = AnnotationUtils.getAnnotationValue(annotation, "version");
        String cron = AnnotationUtils.getAnnotationValue(annotation,"cron");
        String zone = AnnotationUtils.getAnnotationValue(annotation,"zone");
        this.ID = ID(value(producerClass, producerMethod, value), version);
        this.cronSequenceGenerator = new CronSequenceGenerator(cron, zone);
    }

    public A getAnnotation() {
        return annotation;
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public Class<?> getProducerClass() {
        return producerClass;
    }

    public Method getProducerMethod() {
        return producerMethod;
    }

    public Object getConsumer() {
        return consumer;
    }

    public Class<?> getConsumerClass() {
        return consumerClass;
    }

    public Method getConsumerMethod() {
        return consumerMethod;
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
        if (StringUtils.isEmpty(lineName)) {
            lineName = _interface.getName() + "." + method.getName() + "(" + parameterTypeString(method) + ")";
        }
        return lineName;
    }

    protected static String ID(String value, String... items) {
        IDGenerator generator = new IDGenerator(value, "-");
        if (items != null) {
            int len = items.length;
            for (int i = 0; i < len; i++) {
                generator.append(items[i]);
            }
        }
        return generator.generate();
    }
}