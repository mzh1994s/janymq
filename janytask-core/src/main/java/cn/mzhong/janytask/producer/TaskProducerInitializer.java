package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public class TaskProducerInitializer implements TaskComponentInitializer {

    protected void processProducer(TaskContext context, Class<?> producerClass) {
        // 处理生产者
        for (Method method : producerClass.getMethods()) {
            for (TaskAnnotationProcessor annotationProcessor : context.getAnnotationProcessors()) {
                Annotation annotation = method.getAnnotation(annotationProcessor.getAnnotationClass());
                if (annotation != null) {
                    QueueInfo queueInfo = new QueueInfo<Annotation>(
                            annotation,
                            producerClass,
                            method,
                            null,
                            null,
                            null
                    );
                    Map<Method, MessageDao> methodMessageDaoMap = context.getMethodMessageDaoMap();
                    // 注册messageDao
                    MessageDao messageDao = context.getQueueProvider().createMessageDao(queueInfo);
                    queueInfo.setMessageDao(messageDao);

                    // 映射Producer的MessageDao
                    methodMessageDaoMap.put(queueInfo.getProducerMethod(), messageDao);
                    //noinspection SingleStatementInBlock,unchecked
                    annotationProcessor.processProducer(context, queueInfo);
                }
            }
        }

    }

    public void init(TaskContext context) {
        for (Class<?> producerClass : context.getProducerClassSet()) {
            // 注册生产者代理
            Object producer = TaskProducerFactory.newInstance(context, producerClass);
            context.getProducerMap().put(producerClass, producer);
            this.processProducer(context, producerClass);
        }
    }
}
