package cn.mzhong.janytask.consumer;

import cn.mzhong.janytask.core.TaskQueueAnnotationHandler;
import cn.mzhong.janytask.core.TaskComponentInitializer;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.core.TaskWorker;
import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class TaskConsumerInitializer implements TaskComponentInitializer, TaskConsumerCreator {

    final static Logger Log = LoggerFactory.getLogger(TaskConsumerInitializer.class);
    protected TaskContext context;
    protected TaskConsumerCreator consumerCreator = new InternalConsumerCreator();

    /**
     * 消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
     *
     * @param consumerClass
     * @param method
     * @return
     */
    private static <A extends Annotation> QueueInfo<A> findQueueInfo(Object consumer, Class<?> consumerClass, Method method, Class<A> annotationType) {
        Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
        QueueInfo<A> queueInfo = null;
        for (Class<?> _interface : interfaces) {
            try {
                Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
                if (pMethod != null) {
                    A annotation = pMethod.getAnnotation(annotationType);
                    if (annotation != null) {
                        queueInfo = new QueueInfo<A>(
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
        return queueInfo;
    }


    /**
     * 处理消费者
     *
     * @param context
     * @param consumer
     * @param consumerClass
     * @param <A>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <A extends Annotation> Set<TaskExecutor> handleConsumer(
            TaskContext context,
            Object consumer,
            Class<?> consumerClass) {
        Set<TaskExecutor> taskExecutos = new HashSet<TaskExecutor>();
        for (Method method : consumerClass.getMethods()) {
            for (TaskQueueAnnotationHandler annotationProcessor : context.getAnnotationHandlers()) {
                QueueInfo<A> queueInfo = findQueueInfo(
                        consumer,
                        consumerClass,
                        method,
                        annotationProcessor.getAnnotationClass());
                if (queueInfo != null) {
                    // 注册messageDao
                    queueInfo.setMessageDao(context.getQueueProvider().createMessageDao(queueInfo));
                    // 创建消费者线程
                    taskExecutos.add(annotationProcessor.handleConsumer(context, queueInfo));
                    if (Log.isDebugEnabled()) {
                        Log.debug("consumer:'" + queueInfo.ID() + "'inited.");
                    }
                }
            }
        }
        return taskExecutos;
    }

    /**
     * 创建消费者对象
     *
     * @param consumerClass
     * @return
     */
    public Object createConsumer(Class<?> consumerClass) {
        try {
            return consumerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建线程列表（一个消费者消息队列一个线程）
     *
     * @param context
     * @return
     */

    public void init(TaskContext context) {
        TaskWorker taskWorker = context.getTaskWorker();
        Map<Class<?>, Object> consumerMap = context.getConsumerMap();
        try {
            for (Class<?> consumerClass : context.getConsumerClassSet()) {
                Object consumer = this.createConsumer(consumerClass);
                consumerMap.put(consumerClass, consumer);
                taskWorker.addExecutors(this.handleConsumer(context, consumer, consumerClass));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class InternalConsumerCreator implements TaskConsumerCreator {

        public Object createConsumer(Class<?> consumerClass) {
            try {
                return consumerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
