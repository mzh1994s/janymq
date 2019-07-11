package cn.mzhong.janytask.consumer;

import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
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
    private static <A extends Annotation> ConsumerInfo<A> findConsumerInfo(Object consumer, Class<?> consumerClass, Method method, Class<A> annotationType) {
        Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
        ConsumerInfo<A> consumerInfo = null;
        for (Class<?> _interface : interfaces) {
            try {
                Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
                if (pMethod != null) {
                    A annotation = pMethod.getAnnotation(annotationType);
                    if (annotation != null) {
                        consumerInfo = new ConsumerInfo<A>(
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
            Object consumer, Class<?> consumerClass,
            Method method) {
        for (TaskAnnotationProcessor annotationProcessor : context.getAnnotationProcessors()) {
            ConsumerInfo<A> methodInfo = findConsumerInfo(
                    consumer,
                    consumerClass,
                    method,
                    annotationProcessor.getAnnotationClass());
            if (methodInfo != null) {
                annotationProcessor.processConsumer(context, methodInfo);
            }
        }
    }

    protected void initExecutor(TaskContext context, Collection<Object> consumerSet) {
        for (Object consumer : consumerSet) {
            Class<?> consumerClass = consumer.getClass();
            for (Method method : consumerClass.getMethods()) {
                processConsumer(context, consumer, consumerClass, method);
            }
        }
    }

    public void init(TaskContext context) {
        Map<Class<?>, Object> consumerMap = new HashMap<Class<?>, Object>();
        try {
            for (Class<?> consumerClass : context.getConsumerClassSet()) {
                Object consumer = consumerClass.getDeclaredConstructor().newInstance();
                consumerMap.put(consumerClass, consumer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        context.setConsumerMap(consumerMap);
        initExecutor(context, consumerMap.values());
    }
}

