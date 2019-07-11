package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.queue.MessageDao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ProducerInfo<A extends Annotation> {
    protected A annotation;
    protected MessageDao messageDao;
    protected Class<?> producerClass;
    protected Method producerMethod;

    public ProducerInfo(A annotation, Class<?> producerClass, Method producerMethod) {
        this.annotation = annotation;
        this.producerClass = producerClass;
        this.producerMethod = producerMethod;
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
}
