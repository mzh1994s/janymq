package cn.mzhong.janymq.core;

import cn.mzhong.janymq.config.ApplicationConfig;
import cn.mzhong.janymq.config.LooplineConfig;
import cn.mzhong.janymq.config.PiplelineConfig;
import cn.mzhong.janymq.initializer.MQComponentInitializer;
import cn.mzhong.janymq.line.DataSerializer;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LineManagerProvider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class MQContext {
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
    protected LineManagerProvider lineManagerProvider;

    /**
     * 可以给定一个流水线管理器中的序列器，默认序列器为JDK序列器
     */
    protected DataSerializer dataSerializer;
    /**
     * 消费者map
     */
    protected Map<Class<?>, Object> consumerMap = new HashMap<>();
    protected Set<Class<?>> consumerClassSet = new HashSet<>();
    protected ExecutorService consumerExecutorService;
    protected Map<Class<?>, Object> producerMap = new HashMap<>();
    protected Set<Class<?>> producerClassSet = new HashSet<>();
    protected Map<String, LineManager> lineManagerMap = new HashMap<>();

    protected MQComponentInitializer LineManagerInitializer;
    protected MQComponentInitializer consumerInitializer;
    protected MQComponentInitializer producerInitializer;
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

    public LineManagerProvider getLineManagerProvider() {
        return lineManagerProvider;
    }

    public void setLineManagerProvider(LineManagerProvider lineManagerProvider) {
        this.lineManagerProvider = lineManagerProvider;
    }

    public DataSerializer getDataSerializer() {
        return dataSerializer;
    }

    public void setDataSerializer(DataSerializer dataSerializer) {
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

    public Map<String, LineManager> getLineManagerMap() {
        return lineManagerMap;
    }

    public void setLineManagerMap(Map<String, LineManager> lineManagerMap) {
        this.lineManagerMap = lineManagerMap;
    }

    public MQComponentInitializer getLineManagerInitializer() {
        return LineManagerInitializer;
    }

    public void setLineManagerInitializer(MQComponentInitializer lineManagerInitializer) {
        this.LineManagerInitializer = lineManagerInitializer;
    }

    public MQComponentInitializer getConsumerInitializer() {
        return consumerInitializer;
    }

    public void setConsumerInitializer(MQComponentInitializer consumerInitializer) {
        this.consumerInitializer = consumerInitializer;
    }

    public MQComponentInitializer getProducerInitializer() {
        return producerInitializer;
    }

    public void setProducerInitializer(MQComponentInitializer producerInitializer) {
        this.producerInitializer = producerInitializer;
    }


    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
