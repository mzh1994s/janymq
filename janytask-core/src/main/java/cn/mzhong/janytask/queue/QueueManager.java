package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskExecutor;
import cn.mzhong.janytask.queue.loopline.LoopLineAnnotationHandler;
import cn.mzhong.janytask.queue.pipleline.PipleLineAnnotationHandler;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import cn.mzhong.janytask.tool.AnnotationPatternClassScanner;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 队列型任务管理器，管理Pipleline、Loopline等
 */
public class QueueManager extends AbstractQueueManager {

    final static Logger Log = LoggerFactory.getLogger(QueueManager.class);
    /**
     * 用于扫描当前classpath*下包含指定注解的所有类，并在这些类的基础上再次筛选。
     */
    final AnnotationPatternClassScanner scanner = new AnnotationPatternClassScanner();

    public QueueManager() {
        // 注解处理器
        this.annotationHandlers.add(new PipleLineAnnotationHandler());
        this.annotationHandlers.add(new LoopLineAnnotationHandler());
    }

    private void initProducer(Class<?> producerClass, QueueProvider provider) {
        for (Method method : producerClass.getMethods()) {
            Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
            while (iterator.hasNext()) {
                QueueAnnotationHandler annotationProcessor = iterator.next();
                Annotation annotation = method.getAnnotation(annotationProcessor.getAnnotationClass());
                if (annotation != null) {
                    QueueInfo queueInfo = new QueueInfo<Annotation>(annotation, producerClass, method, provider);
                    MessageDao messageDao = queueInfo.getMessageDao();

                    // 映射Producer的MessageDao
                    if (messageDaoMap.containsKey(queueInfo.getProducerMethod())) {
                        throw new RuntimeException("重复的消费者线程！");
                    }
                    messageDaoMap.put(queueInfo.getProducerMethod(), messageDao);
                    //noinspection SingleStatementInBlock,unchecked
                    annotationProcessor.handleProducer(context, this, queueInfo);
                    if (Log.isDebugEnabled()) {
                        Log.debug("producer:'" + queueInfo.ID() + "'inited.");
                    }
                }
            }
        }
        registryProducer(producerClass);
    }

    /**
     * 消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
     *
     * @param consumerClass
     * @param method
     * @return
     */
    private <A extends Annotation> QueueInfo<A> findQueueInfo(Object consumer, Class<?> consumerClass, Method method, Class<A> annotationType, QueueProvider provider) {
        Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
        Iterator<Class<?>> iterator = interfaces.iterator();
        while (iterator.hasNext()) {
            Class<?> _interface = iterator.next();
            try {
                Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
                if (pMethod != null) {
                    A annotation = pMethod.getAnnotation(annotationType);
                    if (annotation != null) {
                        return new QueueInfo<A>(annotation, _interface, pMethod, consumer, consumerClass, method, provider);
                    }
                }
            } catch (NoSuchMethodException e) {
                // pass
            }
        }
        return null;
    }

    private void initConsumer(Class<?> consumerClass, QueueProvider provider) {
        Object consumer = consumerCreator.createConsumer(consumerClass);
        for (Method method : consumerClass.getMethods()) {
            Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
            while (iterator.hasNext()) {
                QueueAnnotationHandler annotationProcessor = iterator.next();
                QueueInfo queueInfo = findQueueInfo(
                        consumer,
                        consumerClass,
                        method,
                        annotationProcessor.getAnnotationClass(),
                        provider);
                if (queueInfo != null) {
                    // 创建消费者线程
                    executors.add(annotationProcessor.handleConsumer(context, this, queueInfo));
                    if (Log.isDebugEnabled()) {
                        Log.debug("consumer:'" + queueInfo.ID() + "'inited.");
                    }
                }
            }
        }
    }

    /**
     * 根据提供商指示的package来扫描与此提供商相关联的生产者、消费者等类对象
     *
     * @param provider
     */
    private void initProvider(QueueProvider provider, Map<Class<?>, QueueProvider> containsMap) throws ClassNotFoundException {
        provider.setContext(context);
        provider.init();
        Set<Class<?>> select = scanner.select(provider.getPackages());
        Iterator<Class<?>> iterator = select.iterator();
        while (iterator.hasNext()) {
            Class<?> component = iterator.next();
            if (containsMap.containsKey(component)) {
                QueueProvider containsProvider = containsMap.get(component);
                throw new OverlapClassException(component, provider, containsProvider);
            }
            containsMap.put(component, provider);
            // 生产者与消费者初始化
            if (component.getAnnotation(Producer.class) != null) {
                initProducer(component, provider);
            } else if (component.getAnnotation(Consumer.class) != null) {
                initConsumer(component, provider);
            }
        }
    }

    private void initScanner() {
        Iterator<QueueProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            scanner.addPackages(iterator.next().getPackages());
        }
        // 预备消费者和生产者注解
        scanner.addAnnotation(Consumer.class, Producer.class);
        scanner.scan();
    }

    /**
     * 支持多个提供商，需要扫描所有提供商，并准备预扫描所有提供商需要的类文件
     *
     * @since 2.0.0
     */
    private void initProviders() throws ClassNotFoundException {
        initScanner();
        Map<Class<?>, QueueProvider> containsMap = new HashMap<Class<?>, QueueProvider>();
        Iterator<QueueProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            initProvider(iterator.next(), containsMap);
        }

    }

    public void init() {
        try {
            initProviders();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // 初始化
//        new QueueManager.ProducerInitializer().init();
//        new QueueManager.ConsumerInitializer().init();
    }

    public Set<TaskExecutor> getTaskExecutors() {
        return executors;
    }


    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    public <T> T getProducer(Class<T> producerClass) {
        Object producer = producerMap.get(producerClass);
        if (producer == null) {
            for (Map.Entry<Class<?>, Object> entry : producerMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(producerClass)) {
                    return (T) entry.getValue();
                }
            }
            throw new NoSuchProducerException("未找到生产者：" + producerClass.getName());
        }
        return (T) producer;
    }
//
//    class ProducerInitializer implements TaskComponent {
//
//        public void setContext(TaskContext context) {
//        }
//
//        protected void processProducer(TaskContext context, Class<?> producerClass) {
//            // 处理生产者
//            for (Method method : producerClass.getMethods()) {
//                Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
//                while (iterator.hasNext()) {
//                    QueueAnnotationHandler annotationProcessor = iterator.next();
//                    Annotation annotation = method.getAnnotation(annotationProcessor.getAnnotationClass());
//                    if (annotation != null) {
//                        QueueInfo queueInfo = new QueueInfo<Annotation>(
//                                annotation,
//                                producerClass,
//                                method,
//                                null,
//                                null,
//                                null
//                        );
//                        // 注册messageDao
//                        MessageDao messageDao = context.getQueueProvider().createMessageDao(queueInfo);
//                        queueInfo.setMessageDao(messageDao);
//
//                        // 映射Producer的MessageDao
//                        methodMessageDaoMap.put(queueInfo.getProducerMethod(), messageDao);
//                        //noinspection SingleStatementInBlock,unchecked
//                        annotationProcessor.handleProducer(context, queueInfo);
//                        if (Log.isDebugEnabled()) {
//                            Log.debug("tproducer:'" + queueInfo.ID() + "'inited.");
//                        }
//                    }
//                }
//            }
//
//        }
//
//        @SuppressWarnings("unchecked")
//        public void init() {
//            QueueManager.this.foreachProducerClassSet(new PInvoker<Class<?>>() {
//                public void invoke(Class<?> producerClass) throws Exception {
//                    // 注册生产者代理
//                    Object tproducer = producerCreator.createProducer(producerClass);
//                    producerMap.put(producerClass, tproducer);
//                    ProducerInitializer.this.processProducer(context, producerClass);
//                }
//            });
//        }
//
//    }
//
//    class ConsumerInitializer implements TaskComponent {
//        public void setContext(TaskContext context) {
//
//        }
//
//        /**
//         * 消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
//         *
//         * @param consumerClass
//         * @param method
//         * @return
//         */
//        private <A extends Annotation> QueueInfo<A> findQueueInfo(Object tconsumer, Class<?> consumerClass, Method method, Class<A> annotationType) {
//            Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
//            QueueInfo<A> queueInfo = null;
//            for (Class<?> _interface : interfaces) {
//                try {
//                    Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
//                    if (pMethod != null) {
//                        A annotation = pMethod.getAnnotation(annotationType);
//                        if (annotation != null) {
//                            queueInfo = new QueueInfo<A>(
//                                    annotation,
//                                    _interface,
//                                    pMethod,
//                                    tconsumer,
//                                    consumerClass,
//                                    method);
//                            break;
//                        }
//                    }
//                } catch (NoSuchMethodException e) {
//                    // pass
//                }
//            }
//            return queueInfo;
//        }
//
//
//        /**
//         * 处理消费者
//         *
//         * @param context
//         * @param tconsumer
//         * @param consumerClass
//         * @param <A>
//         * @return
//         */
//        @SuppressWarnings("unchecked")
//        protected <A extends Annotation> Set<TaskExecutor> handleConsumer(
//                TaskContext context,
//                Object tconsumer,
//                Class<?> consumerClass) {
//            Set<TaskExecutor> taskExecutos = new HashSet<TaskExecutor>();
//            for (Method method : consumerClass.getMethods()) {
//                Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
//                while (iterator.hasNext()) {
//                    QueueAnnotationHandler annotationProcessor = iterator.next();
//                    QueueInfo<A> queueInfo = findQueueInfo(
//                            tconsumer,
//                            consumerClass,
//                            method,
//                            annotationProcessor.getAnnotationClass());
//                    if (queueInfo != null) {
//                        // 注册messageDao
//                        queueInfo.setMessageDao(context.getQueueProvider().createMessageDao(queueInfo));
//                        // 创建消费者线程
//                        taskExecutos.add(annotationProcessor.handleConsumer(context, queueInfo));
//                        if (Log.isDebugEnabled()) {
//                            Log.debug("tconsumer:'" + queueInfo.ID() + "'inited.");
//                        }
//                    }
//                }
//            }
//            return taskExecutos;
//        }
//
//        /**
//         * 创建线程列表（一个消费者消息队列一个线程）
//         *
//         * @return
//         */
//
//        @SuppressWarnings("unchecked")
//        public void init() {
//            final TaskWorker taskWorker = context.getTaskWorker();
//            QueueManager.this.foreachConsumerClassSet(new PInvoker<Class<?>>() {
//                public void invoke(Class<?> consumerClass) throws Exception {
//                    Object tconsumer = consumerCreator.createConsumer(consumerClass);
//                    QueueManager.this.consumerMap.put(consumerClass, tconsumer);
//                    taskWorker.addExecutors(ConsumerInitializer.this.handleConsumer(context, tconsumer, consumerClass));
//                }
//            });
//        }
//    }

}


