package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.core.TaskExecutor;
import cn.mzhong.janytask.core.TaskManager;
import cn.mzhong.janytask.queue.provider.QueueProvider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractQueueManager implements TaskManager {

    protected TaskContext context;
    final Map<Class<?>, Object> consumers = new HashMap<Class<?>, Object>();
    final protected Map<Class<?>, Object> producers = new HashMap<Class<?>, Object>();
    final protected Set<QueueAnnotationHandler> annotationHandlers = new HashSet<QueueAnnotationHandler>();
    final protected Set<QueueProvider> providers = new HashSet<QueueProvider>();
    final protected Map<Method, MessageDao> messageDaoMap = new HashMap<Method, MessageDao>();

    protected ProducerCreator producerCreator = new InternalProducerCreator();
    protected ConsumerCreator consumerCreator = new InternalConsumerCreator();
    protected Set<TaskExecutor> executors = new HashSet<TaskExecutor>();

    public void setContext(TaskContext context) {
        this.context = context;
    }

    public Map<Class<?>, Object> getConsumers() {
        return consumers;
    }

    public Map<Class<?>, Object> getProducers() {
        return producers;
    }

    protected void registryProducer(Class<?> producerClass) {
        producers.put(producerClass, producerCreator.create(producerClass, messageDaoMap));
    }

    public void addProvider(QueueProvider provider) {
        this.providers.add(provider);
    }

}
