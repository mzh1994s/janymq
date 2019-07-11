package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TaskProducerInitializer implements TaskComponentInitializer {

    protected void processProducer(TaskContext context, Class<?> producerClass) {
        // 处理生产者
        for (Method method : producerClass.getMethods()) {
            for (TaskAnnotationProcessor annotationProcessor : context.getAnnotationProcessors()) {
                Annotation annotation = method.getAnnotation(annotationProcessor.getAnnotationClass());
                if (annotation != null) {
                    //noinspection SingleStatementInBlock,unchecked
                    annotationProcessor.processProducer(context, new ProducerInfo<Annotation>(
                            annotation,
                            producerClass,
                            method
                    ));
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
