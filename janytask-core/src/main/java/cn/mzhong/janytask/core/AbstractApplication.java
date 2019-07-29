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

}
