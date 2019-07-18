package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.consumer.Consumer;
import cn.mzhong.janytask.consumer.ConsumerCreator;
import cn.mzhong.janytask.core.TaskComponent;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.core.TaskWorker;
import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.queue.loopline.LoopLineAnnotationHandler;
import cn.mzhong.janytask.queue.pipleline.PipleLineAnnotationHandler;
import cn.mzhong.janytask.producer.Producer;
import cn.mzhong.janytask.producer.ProducerCreator;
import cn.mzhong.janytask.producer.ProducerFactory;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class QueueManager implements TaskComponent {

    final static Logger Log = LoggerFactory.getLogger(QueueManager.class);

    protected TaskContext context;
    protected ConsumerCreator consumerCreator;
    Map<Class<?>, Object> consumerMap = new HashMap<Class<?>, Object>();
    protected Map<Class<?>, Object> producerMap = new HashMap<Class<?>, Object>();
    protected ProducerCreator producerCreator;
    protected Set<QueueAnnotationHandler> annotationHandlers = new HashSet<QueueAnnotationHandler>();
    /**
     * 方法与MessageDao映射Map，在生产者代理中会用到此映射来寻找生产者MessageDao
     */
    protected Map<Method, MessageDao> methodMessageDaoMap = new HashMap<Method, MessageDao>();

    public TaskContext getContext() {
        return context;
    }

    public void setContext(TaskContext context) {
        this.context = context;
    }

    public ConsumerCreator getConsumerCreator() {
        return consumerCreator;
    }

    public void setConsumerCreator(ConsumerCreator consumerCreator) {
        this.consumerCreator = consumerCreator;
    }

    public Map<Class<?>, Object> getConsumerMap() {
        return consumerMap;
    }

    public void setConsumerMap(Map<Class<?>, Object> consumerMap) {
        this.consumerMap = consumerMap;
    }

    public Map<Class<?>, Object> getProducerMap() {
        return producerMap;
    }

    public void setProducerMap(Map<Class<?>, Object> producerMap) {
        this.producerMap = producerMap;
    }

    public ProducerCreator getProducerCreator() {
        return producerCreator;
    }

    public void setProducerCreator(ProducerCreator producerCreator) {
        this.producerCreator = producerCreator;
    }

    public Set<QueueAnnotationHandler> getAnnotationHandlers() {
        return annotationHandlers;
    }

    public void setAnnotationHandlers(Set<QueueAnnotationHandler> annotationHandlers) {
        this.annotationHandlers = annotationHandlers;
    }

    public Map<Method, MessageDao> getMethodMessageDaoMap() {
        return methodMessageDaoMap;
    }

    public void setMethodMessageDaoMap(Map<Method, MessageDao> methodMessageDaoMap) {
        this.methodMessageDaoMap = methodMessageDaoMap;
    }

    public void init(TaskContext context) {
        if (this.producerCreator == null) {
            this.producerCreator = new InternalProducerCreator();
        }
        if (this.consumerCreator == null) {
            this.consumerCreator = new InternalConsumerCreator();
        }
        // 注解处理器
        this.annotationHandlers.add(new PipleLineAnnotationHandler());
        this.annotationHandlers.add(new LoopLineAnnotationHandler());
        // 初始化
        new ProducerInitializer().init(context);
        new ConsumerInitializer().init(context);
    }

    class ProducerInitializer implements TaskComponent {

        protected void processProducer(TaskContext context, Class<?> producerClass) {
            // 处理生产者
            for (Method method : producerClass.getMethods()) {
                Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
                while (iterator.hasNext()) {
                    QueueAnnotationHandler annotationProcessor = iterator.next();
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
                        // 注册messageDao
                        MessageDao messageDao = context.getQueueProvider().createMessageDao(queueInfo);
                        queueInfo.setMessageDao(messageDao);

                        // 映射Producer的MessageDao
                        methodMessageDaoMap.put(queueInfo.getProducerMethod(), messageDao);
                        //noinspection SingleStatementInBlock,unchecked
                        annotationProcessor.handleProducer(context, queueInfo);
                        if (Log.isDebugEnabled()) {
                            Log.debug("producer:'" + queueInfo.ID() + "'inited.");
                        }
                    }
                }
            }

        }

        @SuppressWarnings("unchecked")
        public void init(TaskContext context) {
            String basePackage = context.getApplicationConfig().getBasePackage();
            // 扫描所有的生产者
            Set<Class<?>> producerClassSet = ClassUtils.scanByAnnotation(basePackage, Producer.class);
            for (Class<?> producerClass : producerClassSet) {
                // 注册生产者代理
                Object producer = producerCreator.createProducer(producerClass);
                producerMap.put(producerClass, producer);
                this.processProducer(context, producerClass);
            }
        }

    }

    class ConsumerInitializer implements TaskComponent {

        /**
         * 消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
         *
         * @param consumerClass
         * @param method
         * @return
         */
        private <A extends Annotation> QueueInfo<A> findQueueInfo(Object consumer, Class<?> consumerClass, Method method, Class<A> annotationType) {
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
                Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
                while (iterator.hasNext()) {
                    QueueAnnotationHandler annotationProcessor = iterator.next();
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

        @SuppressWarnings("unchecked")
        public void init(TaskContext context) {
            String basePackage = context.getApplicationConfig().getBasePackage();
            Set<Class<?>> consumerClassSet = ClassUtils.scanByAnnotation(basePackage, Consumer.class);
            TaskWorker taskWorker = context.getTaskWorker();
            try {
                for (Class<?> consumerClass : consumerClassSet) {
                    Object consumer = this.createConsumer(consumerClass);
                    consumerMap.put(consumerClass, consumer);
                    taskWorker.addExecutors(this.handleConsumer(context, consumer, consumerClass));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class InternalProducerCreator implements ProducerCreator {

        public Object createProducer(Class<?> _class) {
            return ProducerFactory.newInstance(methodMessageDaoMap, _class);
        }
    }

    class InternalConsumerCreator implements ConsumerCreator {

        public Object createConsumer(Class<?> consumerClass) {
            try {
                return consumerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}


