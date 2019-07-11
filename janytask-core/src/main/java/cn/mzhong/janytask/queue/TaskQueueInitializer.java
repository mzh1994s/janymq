package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;
import cn.mzhong.janytask.producer.ProducerInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TaskQueueInitializer implements TaskComponentInitializer {

    private void processProducer(TaskContext context, Class<?> producerClass, Method method) {
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

    public void init(TaskContext context) {
        QueueProvider provider = context.getQueueProvider();
        provider.init(context);
        for (Class<?> producerClass : context.getProducerClassSet()) {
            for (Method method : producerClass.getMethods()) {
                this.processProducer(context, producerClass, method);
            }
        }
    }
}
