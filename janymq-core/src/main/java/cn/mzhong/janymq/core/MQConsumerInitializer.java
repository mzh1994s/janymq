package cn.mzhong.janymq.core;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.executor.MQExecutorService;
import cn.mzhong.janymq.executor.MQLooplineExecutor;
import cn.mzhong.janymq.executor.MQPiplelineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class MQConsumerInitializer implements MQComponentInitializer {

    final static Logger Log = LoggerFactory.getLogger(MQConsumerInitializer.class);

    /**
     * 扫描所有父类、接口，注解
     *
     * @param consumerClass
     * @param method
     * @return
     */
    protected static <A> A findAnnotation(Class<?> consumerClass, Method method, Class<? extends Annotation> annotationType) {
        Annotation annotation = method.getAnnotation(annotationType);
        if (annotation == null) {
            Class<?>[] parents = consumerClass.getInterfaces();
            Class<?> superClass = consumerClass.getSuperclass();
            if (superClass != null) {
                parents = Arrays.copyOf(parents, parents.length + 1);
                parents[parents.length - 1] = superClass;
            }
            for (Class<?> parent : parents) {
                try {
                    Method pMethod = parent.getMethod(method.getName(), method.getParameterTypes());
                    if (pMethod != null) {
                        annotation = findAnnotation(parent, pMethod, annotationType);
                        if (annotation != null) {
                            break;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
        }
        return (A) annotation;
    }

    protected void initExecutor(MQContext context, Collection<Object> consumerSet) {
        ExecutorService consumerExecutor = new MQExecutorService("JSimpleMQ-Executor");
        for (Object consumer : consumerSet) {
            Class<?> consumerClass = consumer.getClass();
            for (Method method : consumerClass.getMethods()) {
                Pipleline pipleline = findAnnotation(consumerClass, method, Pipleline.class);
                if (pipleline != null) {
                    consumerExecutor.execute(new MQPiplelineExecutor(context, consumer, method, pipleline));
                    continue;
                }
                Loopline loopline = findAnnotation(consumerClass, method, Loopline.class);
                if (loopline != null) {
                    consumerExecutor.execute(new MQLooplineExecutor(context, consumer, method, loopline));
                }
            }
        }
        context.setConsumerExecutorService(consumerExecutor);
    }

    @Override
    public void init(MQContext context) {
        Map<Class<?>, Object> consumerMap = new HashMap<>();
        try {
            for (Class<?> consumerClass : context.getConsumerClassSet()) {
                Object consumer = consumerClass.getDeclaredConstructor().newInstance();
                consumerMap.put(consumerClass, consumer);
            }
        } catch (Exception e) {
            Log.error("消费者初始化异常", e);
        }
        context.setConsumerMap(consumerMap);
        initExecutor(context, consumerMap.values());
    }
}
