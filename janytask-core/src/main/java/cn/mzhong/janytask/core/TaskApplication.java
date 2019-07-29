package cn.mzhong.janytask.core;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Janytask
 *
 * @author mzhong
 * @version 2.0.0
 * @date 2019年7月10日
 */
public class TaskApplication extends AbstractApplication {

    final static Logger Log = LoggerFactory.getLogger(TaskApplication.class);

    protected void wellcome() {
        System.out.println("janytask application started!");
    }

    public TaskApplication() {
        this.queueManager.setContext(context);
        this.scheduleManager.setContext(context);
        this.taskWorker.setContext(context);
    }

    public void start() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }

        // 初始化队列型任务管理器
        this.queueManager.init();
        // 初始化定时任务管理器
        this.scheduleManager.init();

        // 收集执行者
        Set<TaskExecutor> executors = new HashSet<TaskExecutor>();
        executors.addAll(this.queueManager.getTaskExecutors());
        executors.addAll(this.scheduleManager.getTaskExecutors());
        this.taskWorker.addExecutors(executors);

        // 初始化线程调度器
        this.taskWorker.init();

        // 正常终结
        Runtime.getRuntime().addShutdownHook(new TaskShutdownHook(context));

        // 启动线程调度器
        this.taskWorker.start();

        // 欢迎使用
        this.wellcome();
    }

    public <T> T getProducer(Class<T> producerClass) {
        return this.queueManager.getProducer(producerClass);
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
