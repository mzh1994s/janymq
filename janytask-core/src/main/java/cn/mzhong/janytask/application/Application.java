package cn.mzhong.janytask.application;

import cn.mzhong.janytask.queue.QueueManager;
import cn.mzhong.janytask.schedule.ScheduleManager;
import cn.mzhong.janytask.worker.TaskWorker;

public abstract class Application {

    /**
     * 应用程序配置
     */
    protected String name;

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

    /**
     * 正常终结者
     *
     * @since 2.0.0
     */
    final protected TaskShutdownHook shutdownHook = new TaskShutdownHook(this, context);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskContext getContext() {
        return context;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    public TaskWorker getTaskWorker() {
        return taskWorker;
    }

    public abstract void start();

    public abstract void close();
}
