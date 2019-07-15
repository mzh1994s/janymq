package cn.mzhong.janytask.consumer;

import cn.mzhong.janytask.core.TaskAnnotationHandler;
import cn.mzhong.janytask.core.TaskComponentInitializer;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.executor.TaskExecutorService;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TaskConsumerInitializer implements TaskComponentInitializer {

    final static Logger Log = LoggerFactory.getLogger(TaskConsumerInitializer.class);
    protected TaskContext context;

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
    protected <A extends Annotation> List<TaskExecutor<A>> handleConsumer(
            TaskContext context,
            Object consumer,
            Class<?> consumerClass) {
        List<TaskExecutor<A>> taskList = new ArrayList<TaskExecutor<A>>();
        for (Method method : consumerClass.getMethods()) {
            for (TaskAnnotationHandler annotationProcessor : context.getAnnotationHandlers()) {
                QueueInfo<A> queueInfo = findQueueInfo(
                        consumer,
                        consumerClass,
                        method,
                        annotationProcessor.getAnnotationClass());
                if (queueInfo != null) {
                    // 注册messageDao
                    queueInfo.setMessageDao(context.getQueueProvider().createMessageDao(queueInfo));
                    // 创建消费者线程
                    taskList.add(annotationProcessor.handleConsumer(context, queueInfo));
                    if (Log.isDebugEnabled()) {
                        Log.debug("consumer:'" + queueInfo.ID() + "'inited.");
                    }
                }
            }
        }
        return taskList;
    }

    /**
     * 创建消费者对象
     *
     * @param consumerClass
     * @return
     */
    protected Object createConsumer(Class<?> consumerClass) {
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
    protected List<TaskExecutor<? extends Annotation>> createTaskList(TaskContext context) {
        List<TaskExecutor<? extends Annotation>> taskList = new ArrayList<TaskExecutor<? extends Annotation>>();
        Map<Class<?>, Object> consumerMap = context.getConsumerMap();
        try {
            for (Class<?> consumerClass : context.getConsumerClassSet()) {
                Object consumer = this.createConsumer(consumerClass);
                consumerMap.put(consumerClass, consumer);
                taskList.addAll(this.handleConsumer(context, consumer, consumerClass));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return taskList;
    }

    protected void runTaskExecutors(TaskContext context, List<TaskExecutor<? extends Annotation>> taskList) {
        // 初始化线程池
        int nThreads = taskList.size();
        TaskExecutorService taskExecutorService = new TaskExecutorService(nThreads);
        context.setConsumerExecutorService(taskExecutorService);
        for (int i = 0; i < nThreads; i++) {
            taskExecutorService.execute(taskList.get(i));
        }
    }

    public void init(TaskContext context) {
        runTaskExecutors(context, this.createTaskList(context));
    }
}

