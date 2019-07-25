package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.core.TaskManager;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import cn.mzhong.janytask.tool.PInvoker;
import cn.mzhong.janytask.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractQueueManager implements TaskManager {

    protected TaskContext context;
    protected ConsumerCreator consumerCreator;
    Map<Class<?>, Object> consumerMap = new HashMap<Class<?>, Object>();
    protected Map<Class<?>, Object> producerMap = new HashMap<Class<?>, Object>();
    protected InstanceCreator producerCreator;
    protected Set<QueueAnnotationHandler> annotationHandlers = new HashSet<QueueAnnotationHandler>();
    protected Set<Class<?>> producerClassSet = new HashSet<Class<?>>();
    protected Set<Class<?>> consumerClassSet = new HashSet<Class<?>>();
    // 方法与MessageDao映射Map，在生产者代理中会用到此映射来寻找生产者MessageDao
    protected Map<Method, MessageDao> methodMessageDaoMap = new HashMap<Method, MessageDao>();

    protected Set<QueueProvider> providers = new HashSet<QueueProvider>();

    protected Map<QueueProvider, ProviderInfo> providerInfoMap = new HashMap<QueueProvider, ProviderInfo>();

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

    public InstanceCreator getProducerCreator() {
        return producerCreator;
    }

    public void setProducerCreator(InstanceCreator producerCreator) {
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

    public void addProvider(QueueProvider provider) {
        this.providers.add(provider);
    }

    private void foreachComponentClassSet(
            Set<Class<?>> classSet,
            PInvoker<Class<?>> classPInvoker,
            Class<? extends Annotation> annotationClass) {
        if (classSet.isEmpty()) {
            String basePackage = context.getApplicationConfig().getName();
            classSet.addAll(ClassUtils.scanByAnnotation(basePackage, annotationClass));
        }
        Iterator<Class<?>> iterator = classSet.iterator();
        while (iterator.hasNext()) {
            try {
                classPInvoker.invoke(iterator.next());
            } catch (Exception e) {
                //
            }
        }
    }

    public void foreachProducerClassSet(PInvoker<Class<?>> classPInvoker) {
        this.foreachComponentClassSet(producerClassSet, classPInvoker, Producer.class);
    }


    public void foreachConsumerClassSet(PInvoker<Class<?>> classPInvoker) {
        this.foreachComponentClassSet(consumerClassSet, classPInvoker, Consumer.class);
    }

}
