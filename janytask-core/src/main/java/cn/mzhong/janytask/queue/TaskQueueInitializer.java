package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TaskQueueInitializer implements TaskComponentInitializer {

    private List<QueueAnnotationProcessor<? extends Annotation>> annotationProcessors;

    public TaskQueueInitializer() {
        this.annotationProcessors = new ArrayList<QueueAnnotationProcessor<? extends Annotation>>();
        this.addAnnotationProcessor(new PipleLineProcessor());
        this.addAnnotationProcessor(new LoopLineProcessor());
    }

    private void annotationProcessorInvoke(TaskContext context, Class<?> producerClass, Method method) {
        for (QueueAnnotationProcessor annotationProcessor : annotationProcessors) {
            Annotation annotation = method.getAnnotation(annotationProcessor.getAnnotationClass());
            if (annotation != null) {
                //noinspection SingleStatementInBlock,unchecked
                annotationProcessor.invoke(context, producerClass, method, annotation);
            }
        }
    }

    public void addAnnotationProcessor(QueueAnnotationProcessor<? extends Annotation> annotationProcessor) {
        this.annotationProcessors.add(annotationProcessor);
    }

    public void init(TaskContext context) {
        QueueProvider provider = context.getQueueProvider();
        provider.init(context);
        for (Class<?> producerClass : context.getProducerClassSet()) {
            for (Method method : producerClass.getMethods()) {
                annotationProcessorInvoke(context, producerClass, method);
            }
        }
    }
}
