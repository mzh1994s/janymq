package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.Application;
import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.application.TaskContextAware;
import cn.mzhong.janytask.queue.ack.AckHandler;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import cn.mzhong.janytask.worker.TaskExecutor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractQueueManager implements TaskContextAware {

    protected TaskContext context;
    final protected Application application;
    final protected Set<QueueAnnotationHandler> annotationHandlers = new HashSet<QueueAnnotationHandler>();
    final protected Set<QueueProvider> providers = new HashSet<QueueProvider>();
    final protected Map<Method, MessageDao> messageDaoMap = new HashMap<Method, MessageDao>();
    final protected Set<TaskExecutor> executors = new HashSet<TaskExecutor>();
    final protected AckHandler ackHandler = new AckHandler();

    protected ProducerFactory producerFactory = new InternalProducerFactory();
    protected ConsumerFactory consumerFactory = new InternalConsumerFactory();

    public AbstractQueueManager(Application application) {
        this.application = application;
    }

    public void setContext(TaskContext context) {
        this.context = context;
        this.ackHandler.setContext(context);
        this.producerFactory.setContext(context);
        this.consumerFactory.setContext(context);
    }

    public void addProvider(QueueProvider provider) {
        this.providers.add(provider);
    }

    public Map<Method, MessageDao> getMessageDaoMap() {
        return messageDaoMap;
    }

    public AckHandler getAckHandler() {
        return ackHandler;
    }

    public ProducerFactory getProducerFactory() {
        return producerFactory;
    }

    public void setProducerFactory(ProducerFactory producerFactory) {
        this.producerFactory = producerFactory;
    }

    public ConsumerFactory getConsumerFactory() {
        return consumerFactory;
    }

    public void setConsumerFactory(ConsumerFactory consumerFactory) {
        this.consumerFactory = consumerFactory;
    }
}
