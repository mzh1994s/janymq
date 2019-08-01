package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.Application;
import cn.mzhong.janytask.queue.pipeline.PipelineAnnotationHandler;
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
    final protected AnnotationPatternClassScanner scanner = new AnnotationPatternClassScanner();

    public QueueManager(Application application) {
        super(application);
        // 注解处理器
        this.annotationHandlers.add(new PipelineAnnotationHandler());
    }

    /**
     * 初始化生产者执行者（生产者方法）
     *
     * @param producerClass
     * @param method
     * @param provider
     */
    private void initProducerInvoker(Class<?> producerClass, Method method, QueueProvider provider) {
        // 将所有注解处理器匹配
        Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
        while (iterator.hasNext()) {
            QueueAnnotationHandler annotationProcessor = iterator.next();
            Annotation annotation = method.getAnnotation(annotationProcessor.getAnnotationClass());
            if (annotation != null) {
                // 生产线
                QueueInfo queueInfo = new QueueInfo<Annotation>(annotation, producerClass, method, provider);
                MessageDao messageDao = queueInfo.getMessageDao();

                // 映射Producer的MessageDao
                if (messageDaoMap.containsKey(queueInfo.getProducerMethod())) {
                    throw new RuntimeException("重复的生产线：" + queueInfo.getProducerMethod());
                }
                messageDaoMap.put(queueInfo.getProducerMethod(), messageDao);
                //noinspection SingleStatementInBlock,unchecked
                annotationProcessor.handleProducer(context, this, queueInfo);
                if (Log.isDebugEnabled()) {
                    Log.debug("producer:'" + queueInfo.getId() + "'inited.");
                }
                break;
            }
        }
    }

    /**
     * 初始化生产者
     *
     * @param producerClass
     * @param provider
     */
    private void initProducer(Class<?> producerClass, QueueProvider provider) {
        producerFactory.registryProducer(producerClass);
        for (Method method : producerClass.getMethods()) {
            initProducerInvoker(producerClass, method, provider);
        }
    }

    /**
     * 消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
     *
     * @param consumerClass
     * @param method
     * @return
     */
    private <A extends Annotation> QueueInfo<A> findQueueInfo(Class<?> consumerClass, Method method,
                                                              Class<A> annotationType, QueueProvider provider) {
        Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
        Iterator<Class<?>> iterator = interfaces.iterator();
        while (iterator.hasNext()) {
            Class<?> _interface = iterator.next();
            try {
                Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
                if (pMethod != null) {
                    A annotation = pMethod.getAnnotation(annotationType);
                    if (annotation != null) {
                        return new QueueInfo<A>(annotation, _interface, pMethod, consumerClass, method, provider);
                    }
                }
            } catch (NoSuchMethodException e) {
                // pass
            }
        }
        return null;
    }

    /**
     * 初始化消费者执行者
     *
     * @param consumerClass
     * @param method
     * @param provider
     */
    private void initConsumerInvoker(Class<?> consumerClass, Method method, QueueProvider provider) {
        Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
        while (iterator.hasNext()) {
            QueueAnnotationHandler annotationProcessor = iterator.next();
            QueueInfo queueInfo = findQueueInfo(consumerClass, method, annotationProcessor.getAnnotationClass(), provider);
            if (queueInfo != null) {
                // 创建消费者线程
                executors.add(annotationProcessor.handleConsumer(context, this, queueInfo));
                if (Log.isDebugEnabled()) {
                    Log.debug("consumer:'" + queueInfo.getId() + "'inited.");
                }
                break;
            }
        }
    }

    /**
     * 初始化消费者
     *
     * @param consumerClass
     * @param provider
     */
    private void initConsumer(Class<?> consumerClass, QueueProvider provider) {
        consumerFactory.registryConsumer(consumerClass);
        for (Method method : consumerClass.getMethods()) {
            initConsumerInvoker(consumerClass, method, provider);
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

    /**
     * 初始化类扫描器，将所有提供商的包收集起来，扫描所有提供商的类，稍后使用{@link #scanner}的
     * {@link AnnotationPatternClassScanner#select(String[])}方法可进行进一波的筛选
     */
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
        Map<Class<?>, QueueProvider> containsMap = new HashMap<Class<?>, QueueProvider>();
        Iterator<QueueProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            initProvider(iterator.next(), containsMap);
        }

    }

    public void init() {
        try {
            this.initScanner();
            this.initProviders();
            this.executors.add(ackHandler);
            this.application.getTaskWorker().addExecutors(executors);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    public <T> T getProducer(Class<T> producerClass) {
        return (T) producerFactory.getObject(producerClass);
    }

}


