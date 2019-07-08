package cn.mzhong.janytask.core;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.LooplineConfig;
import cn.mzhong.janytask.config.PiplelineConfig;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;
import cn.mzhong.janytask.queue.JdkDataSerializer;
import cn.mzhong.janytask.queue.LineManager;
import cn.mzhong.janytask.queue.Provider;

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
    protected PiplelineConfig piplelineConfig;
    /**
     * 环线配置项
     */
    protected LooplineConfig looplineConfig;

    /**
     * Redis、Zookeeper或者数据库都可以作为消息传播或者持久化介质，而StoreManager为这些介质的客服端
     * StoreManagerProvider用于配置和提供StoreManager
     */
    protected Provider lineManagerProvider;

    /**
     * 可以给定一个流水线管理器中的序列器，默认序列器为JDK序列器
     */
    protected JdkDataSerializer dataSerializer;
    /**
     * 消费者map
     */
    protected Map<Class<?>, Object> consumerMap = new HashMap<Class<?>, Object>();
    protected Set<Class<?>> consumerClassSet = new HashSet<Class<?>>();
    protected ExecutorService consumerExecutorService;
    protected Map<Class<?>, Object> producerMap = new HashMap<Class<?>, Object>();
    protected Set<Class<?>> producerClassSet = new HashSet<Class<?>>();
    protected Map<Method, LineManager> methodLineManagerMap = new HashMap<Method, LineManager>();

    protected TaskComponentInitializer LineManagerInitializer;
    protected TaskComponentInitializer consumerInitializer;
    protected TaskComponentInitializer producerInitializer;
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

    public PiplelineConfig getPiplelineConfig() {
        return piplelineConfig;
    }

    public void setPiplelineConfig(PiplelineConfig piplelineConfig) {
        this.piplelineConfig = piplelineConfig;
    }

    public LooplineConfig getLooplineConfig() {
        return looplineConfig;
    }

    public void setLooplineConfig(LooplineConfig looplineConfig) {
        this.looplineConfig = looplineConfig;
    }

    public Provider getLineManagerProvider() {
        return lineManagerProvider;
    }

    public void setLineManagerProvider(Provider lineManagerProvider) {
        this.lineManagerProvider = lineManagerProvider;
    }

    public JdkDataSerializer getDataSerializer() {
        return dataSerializer;
    }

    public void setDataSerializer(JdkDataSerializer dataSerializer) {
        this.dataSerializer = dataSerializer;
    }

    public Map<Class<?>, Object> getConsumerMap() {
        return consumerMap;
    }

    public void setConsumerMap(Map<Class<?>, Object> consumerMap) {
        this.consumerMap = consumerMap;
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

    public void setProducerMap(Map<Class<?>, Object> producerMap) {
        this.producerMap = producerMap;
    }

    public Set<Class<?>> getProducerClassSet() {
        return producerClassSet;
    }

    public void setProducerClassSet(Set<Class<?>> producerClassSet) {
        this.producerClassSet = producerClassSet;
    }

    public Map<Method, LineManager> getMethodLineManagerMap() {
        return methodLineManagerMap;
    }

    public void setMethodLineManagerMap(Map<Method, LineManager> methodLineManagerMap) {
        this.methodLineManagerMap = methodLineManagerMap;
    }

    public TaskComponentInitializer getLineManagerInitializer() {
        return LineManagerInitializer;
    }

    public void setLineManagerInitializer(TaskComponentInitializer lineManagerInitializer) {
        this.LineManagerInitializer = lineManagerInitializer;
    }

    public TaskComponentInitializer getConsumerInitializer() {
        return consumerInitializer;
    }

    public void setConsumerInitializer(TaskComponentInitializer consumerInitializer) {
        this.consumerInitializer = consumerInitializer;
    }

    public TaskComponentInitializer getProducerInitializer() {
        return producerInitializer;
    }

    public void setProducerInitializer(TaskComponentInitializer producerInitializer) {
        this.producerInitializer = producerInitializer;
    }


    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
