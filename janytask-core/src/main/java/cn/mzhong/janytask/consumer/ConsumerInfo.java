package cn.mzhong.janytask.consumer;


import cn.mzhong.janytask.producer.ProducerInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ConsumerInfo<A extends Annotation> extends ProducerInfo<A> {
    protected Object consumer;
    protected Class<?> consumerClass;
    protected Method consumerMethod;

    public ConsumerInfo(A annotation, Class<?> producerClass, Method producerMethod, Object consumer, Class<?> consumerClass, Method consumerMethod) {
        super(annotation, producerClass, producerMethod);
        this.consumer = consumer;
        this.consumerClass = consumerClass;
        this.consumerMethod = consumerMethod;
    }

    public A getAnnotation() {
        return annotation;
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
}