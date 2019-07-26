package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.core.TaskManager;
import cn.mzhong.janytask.queue.provider.QueueProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractQueueManager implements TaskManager {

    protected TaskContext context;
    final Map<Class<?>, Object> consumerMap = new HashMap<Class<?>, Object>();
    final protected Map<Class<?>, Object> producerMap = new HashMap<Class<?>, Object>();
    final protected Set<QueueAnnotationHandler> annotationHandlers = new HashSet<QueueAnnotationHandler>();

    final protected Set<QueueProvider> providers = new HashSet<QueueProvider>();

    final protected Map<QueueProvider, ProviderInfo> providerInfoMap = new HashMap<QueueProvider, ProviderInfo>();

    public void setContext(TaskContext context) {
        this.context = context;
    }

    public Map<Class<?>, Object> getConsumerMap() {
        return consumerMap;
    }

    public Map<Class<?>, Object> getProducerMap() {
        return producerMap;
    }

    public void addProvider(QueueProvider provider) {
        this.providers.add(provider);
    }

}
