package cn.mzhong.janytask.core;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.QueueConfig;
import cn.mzhong.janytask.queue.JdkDataSerializer;
import cn.mzhong.janytask.queue.QueueManager;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import cn.mzhong.janytask.schedule.ScheduleManager;

import java.util.Set;

public abstract class TaskContext {

    /**
     * 应用程序配置项
     */
    protected ApplicationConfig applicationConfig = new ApplicationConfig();
    /**
     * 流水线配置项
     */
    protected QueueConfig queueConfig = new QueueConfig();

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
     * 队列型任务管理器
     *
     * @since 2.0.0
     */
    protected QueueManager queueManager = new QueueManager();
    /**
     * 定时型任务管理器
     *
     * @since 2.0.0
     */
    protected ScheduleManager scheduleManager = new ScheduleManager();
    /**
     * 任务调度器
     *
     * @since 2.0.0
     */
    protected TaskWorker taskWorker = new TaskWorker();

    /**
     * 应用程序终结标志
     *
     * @since 1.0.0
     */
    protected volatile boolean shutdown = false;

    protected TaskShutdownHook shutdownHook = new TaskShutdownHook(this);

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

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    public void setScheduleManager(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

    public JdkDataSerializer getDataSerializer() {
        return dataSerializer;
    }

    public void setDataSerializer(JdkDataSerializer dataSerializer) {
        this.dataSerializer = dataSerializer;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public TaskWorker getTaskWorker() {
        return taskWorker;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public void addShutdownHook(Runnable runnable) {
        shutdownHook.add(runnable);
    }
}
