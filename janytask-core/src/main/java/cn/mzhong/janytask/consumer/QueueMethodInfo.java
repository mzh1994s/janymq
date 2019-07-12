package cn.mzhong.janytask.consumer;


import cn.mzhong.janytask.queue.MessageDao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class QueueMethodInfo<A extends Annotation> {
    protected A annotation;
    protected MessageDao messageDao;
    protected Class<?> producerClass;
    protected Method producerMethod;
    protected Object consumer;
    protected Class<?> consumerClass;
    protected Method consumerMethod;


    public QueueMethodInfo(A annotation, Class<?> producerClass, Method producerMethod, Object consumer, Class<?> consumerClass, Method consumerMethod) {
        this.annotation = annotation;
        this.producerClass = producerClass;
        this.producerMethod = producerMethod;
        this.consumer = consumer;
        this.consumerClass = consumerClass;
        this.consumerMethod = consumerMethod;
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
}