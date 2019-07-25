package cn.mzhong.janytask.core;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.queue.NoSuchProducerException;
import cn.mzhong.janytask.queue.QueueManager;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import cn.mzhong.janytask.schedule.ScheduleManager;

import java.util.Map;

public abstract class AbstractApplication {

    /**
     * 应用程序配置
     */
    protected ApplicationConfig applicationConfig;

    /**
     * 应用程序上下文
     */
    final protected TaskContext context = new InternalContext();

    /**
     * 队列型任务管理器
     *
     * @since 2.0.0
     */
    final protected QueueManager queueManager = new QueueManager();
    /**
     * 定时型任务管理器
     *
     * @since 2.0.0
     */
    final protected ScheduleManager scheduleManager = new ScheduleManager();
    /**
     * 任务调度器
     *
     * @since 2.0.0
     */
    final protected TaskWorker taskWorker = new TaskWorker();

    protected void wellcome() {
        System.out.println("janytask application started!");
    }

    public AbstractApplication() {
        this.queueManager.setContext(context);
        this.scheduleManager.setContext(context);
        this.taskWorker.setContext(context);
    }

    protected abstract void initQueueManager();
    protected abstract void initScheduleManager();
    protected abstract void initTaskWorker();

    public void start() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }
        initQueueManager();
        initScheduleManager();
        initTaskWorker();

        // 正常终结
        Runtime.getRuntime().addShutdownHook(new TaskShutdownHook(context));

        // 启动worker
        this.taskWorker.start();
        this.wellcome();
    }

    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    public <T> T getProducer(Class<T> producerClass) {
        Map<Class<?>, Object> producerMap = this.queueManager.getProducerMap();
        Object producer = producerMap.get(producerClass);
        if (producer == null) {
            for (Map.Entry<Class<?>, Object> entry : producerMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(producerClass)) {
                    return (T) entry.getValue();
                }
            }
            throw new NoSuchProducerException("未在当前上下文中找到生产者：" + producerClass.getName());
        }
        return (T) producer;
    }

    public void close() {
        synchronized (context) {
            if (!context.isShutdown()) {
                context.shutdownHook.run();
            }
        }
    }

    /**
     * 添加一个提供商到应用程序
     *
     * @param provider
     * @since 2.0.0
     */
    public void addProvider(QueueProvider provider) {
        queueManager.addProvider(provider);
    }

}
