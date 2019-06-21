package cn.mzhong.janymq.initializer;

import cn.mzhong.janymq.line.Loopline;
import cn.mzhong.janymq.line.Pipleline;
import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.executor.MQExecutorService;
import cn.mzhong.janymq.executor.MQLooplineExecutor;
import cn.mzhong.janymq.executor.MQPiplelineExecutor;
import cn.mzhong.janymq.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;


public class MQConsumerInitializer implements MQComponentInitializer {

    final static Logger Log = LoggerFactory.getLogger(MQConsumerInitializer.class);

    /**
     * 在JAnyMQ中，消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
     *
     * @param consumerClass
     * @param method
     * @return
     */
    protected static <A extends Annotation> LineInfo findLineInfo(Class<?> consumerClass, Method method, Class<A> annotationType) {
        Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
        LineInfo lineInfo = null;
        for (Class<?> _interface : interfaces) {
            try {
                Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
                if (pMethod != null) {
                    Annotation annotation = pMethod.getAnnotation(annotationType);
                    if (annotation != null) {
                        lineInfo = new LineInfo(_interface, pMethod, annotation);
                        break;
                    }
                }
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        return lineInfo;
    }

    protected static Pipleline findPipleline(Class<?> consumerClass, Method method) {
        LineInfo<cn.mzhong.janymq.annotation.Pipleline> lineInfo =
                findLineInfo(consumerClass, method, cn.mzhong.janymq.annotation.Pipleline.class);
        if (lineInfo != null) {
            cn.mzhong.janymq.annotation.Pipleline piplelineAnnotation = lineInfo.annotation;
            return new Pipleline(lineInfo._interface, lineInfo.method, piplelineAnnotation);
        }
        return null;
    }

    protected static Loopline findLoopline(Class<?> consumerClass, Method method) {
        LineInfo<cn.mzhong.janymq.annotation.Loopline> lineInfo =
                findLineInfo(consumerClass, method, cn.mzhong.janymq.annotation.Loopline.class);
        if (lineInfo != null) {
            cn.mzhong.janymq.annotation.Loopline looplineAnnotation = lineInfo.annotation;
            return new Loopline(lineInfo._interface, lineInfo.method, looplineAnnotation);
        }
        return null;
    }

    protected void initExecutor(MQContext context, Collection<Object> consumerSet) {
        ExecutorService consumerExecutor = new MQExecutorService("JSimpleMQ-Executor");
        for (Object consumer : consumerSet) {
            Class<?> consumerClass = consumer.getClass();
            for (Method method : consumerClass.getMethods()) {
                Pipleline pipleline = findPipleline(consumerClass, method);
                if (pipleline != null) {
                    consumerExecutor.execute(new MQPiplelineExecutor(context, consumer, method, pipleline));
                    continue;
                }
                Loopline loopline = findLoopline(consumerClass, method);
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

class LineInfo<A extends Annotation> {
    Class<?> _interface;
    Method method;
    A annotation;

    public LineInfo(Class<?> _class, Method method, A annotation) {
        this._interface = _class;
        this.method = method;
        this.annotation = annotation;
    }
}
