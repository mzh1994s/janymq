package cn.mzhong.janytask.core;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.QueueConfig;
import cn.mzhong.janytask.queue.JdkDataSerializer;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueProvider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class TaskContext {
    /**
     * 应用程序配置项
     */
    protected ApplicationConfig applicationConfig;
    /**
     * 流水线配置项
     */
    protected QueueConfig queueConfig;

    /**
     * Redis、Zookeeper或者数据库都可以作为消息传播或者持久化介质，而queueProvider为这些介质的客服端
     * queueProvider用于配置和提供queueManager
     */
    protected QueueProvider queueProvider;

    /**
     * 可以给定一个流水线管理器中的序列器，默认序列器为JDK序列器
     */
    protected JdkDataSerializer dataSerializer;
    /**
     * 注解处理器
     */
    protected Set<TaskAnnotationHandler> annotationHandlers = new HashSet<TaskAnnotationHandler>();
    /**
     * 消费者
     */
    protected Map<Class<?>, Object> consumerMap = new HashMap<Class<?>, Object>();
    protected Set<Class<?>> consumerClassSet;
    /**
     * 生产者
     */
    protected Map<Class<?>, Object> producerMap = new HashMap<Class<?>, Object>();
    protected Set<Class<?>> producerClassSet;

    /**
     * 方法与MessageDao映射Map，在生产者代理中会用到此映射来寻找生产者MessageDao
     */
    protected Map<Method, MessageDao> methodMessageDaoMap = new HashMap<Method, MessageDao>();

    /**
     * 消费者线程池
     */
    protected ExecutorService consumerExecutorService;

    /**
     * 生产者初始化程序，用于生成生产者代理
     */
    protected TaskComponentInitializer producerInitializer;
    /**
     * 消费者初始化程序，用于创建消费者线程
     */
    protected TaskComponentInitializer consumerInitializer;
    /**
     * JSimpleMQ应用程序是否已经终结
     */
    protected volatile boolean shutdown = false;

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public QueueConfig getQueueConfig() {
        return queueConfig;
    }

    public void setQueueConfig(QueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }

    public QueueProvider getQueueProvider() {
        return queueProvider;
    }

    public void setQueueProvider(QueueProvider queueProvider) {
        this.queueProvider = queueProvider;
    }

    public JdkDataSerializer getDataSerializer() {
        return dataSerializer;
    }

    public void setDataSerializer(JdkDataSerializer dataSerializer) {
        this.dataSerializer = dataSerializer;
    }

    public Set<TaskAnnotationHandler> getAnnotationHandlers() {
        return annotationHandlers;
    }

    public void addAnnotationHandler(TaskAnnotationHandler annotationHandler) {
        this.annotationHandlers.add(annotationHandler);
    }

    public Map<Class<?>, Object> getConsumerMap() {
        return consumerMap;
    }

    public Set<Class<?>> getConsumerClassSet() {
        return consumerClassSet;
    }

    public void setConsumerClassSet(Set<Class<?>> consumerClassSet) {
        this.consumerClassSet = consumerClassSet;
    }

    public ExecutorService getConsumerExecutorService() {
        return consumerExecutorService;
    }

    public void setConsumerExecutorService(ExecutorService consumerExecutorService) {
        this.consumerExecutorService = consumerExecutorService;
    }

    public Map<Class<?>, Object> getProducerMap() {
        return producerMap;
    }

    public Set<Class<?>> getProducerClassSet() {
        return producerClassSet;
    }

    public void setProducerClassSet(Set<Class<?>> producerClassSet) {
        this.producerClassSet = producerClassSet;
    }

    public Map<Method, MessageDao> getMethodMessageDaoMap() {
        return methodMessageDaoMap;
    }

    public TaskComponentInitializer getProducerInitializer() {
        return producerInitializer;
    }

    public void setProducerInitializer(TaskComponentInitializer producerInitializer) {
        this.producerInitializer = producerInitializer;
    }

    public TaskComponentInitializer getConsumerInitializer() {
        return consumerInitializer;
    }

    public void setConsumerInitializer(TaskComponentInitializer consumerInitializer) {
        this.consumerInitializer = consumerInitializer;
    }


    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
