package cn.mzhong.janytask.consumer;

import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;


public class TaskConsumerInitializer implements TaskComponentInitializer {

    final static Logger Log = LoggerFactory.getLogger(TaskConsumerInitializer.class);

    /**
     * 消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
     *
     * @param consumerClass
     * @param method
     * @return
     */
    private static <A extends Annotation> QueueMethodInfo<A> findConsumerInfo(Object consumer, Class<?> consumerClass, Method method, Class<A> annotationType) {
        Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
        QueueMethodInfo<A> consumerInfo = null;
        for (Class<?> _interface : interfaces) {
            try {
                Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
                if (pMethod != null) {
                    A annotation = pMethod.getAnnotation(annotationType);
                    if (annotation != null) {
                        consumerInfo = new QueueMethodInfo<A>(
                                annotation,
                                _interface,
                                pMethod,
                                consumer,
                                consumerClass,
                                method);
                        break;
                    }
                }
            } catch (NoSuchMethodException e) {
                // pass
            }
        }
        return consumerInfo;
    }


    @SuppressWarnings("unchecked")
    protected <A extends Annotation> void processConsumer(
            TaskContext context,
            Object consumer,
            Class<?> consumerClass) {
        for (Method method : consumerClass.getMethods()) {
            for (TaskAnnotationProcessor annotationProcessor : context.getAnnotationProcessors()) {
                QueueMethodInfo<A> methodInfo = findConsumerInfo(
                        consumer,
                        consumerClass,
                        method,
                        annotationProcessor.getAnnotationClass());
                if (methodInfo != null) {
                    annotationProcessor.processConsumer(context, methodInfo);
                }
            }
        }
    }

    public void init(TaskContext context) {
        Map<Class<?>, Object> consumerMap = context.getConsumerMap();
        try {
            for (Class<?> consumerClass : context.getConsumerClassSet()) {
                Object consumer = consumerClass.getDeclaredConstructor().newInstance();
                consumerMap.put(consumerClass, consumer);
                this.processConsumer(context, consumer, consumerClass);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

