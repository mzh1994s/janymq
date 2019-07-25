package cn.mzhong.janytask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Janytask
 *
 * @author mzhong
 * @version 2.0.0
 * @date 2019年7月10日
 */
public class TaskApplication extends AbstractApplication {

    final static Logger Log = LoggerFactory.getLogger(TaskApplication.class);

    /**
     * <p>
     * 初始化队列任务管理器，稍后在任务调度器中会使用到此组件初始化之后的数据。
     * <p/>
     * <p>
     * 数据包括：<br/>
     * 1、一个TaskExecutor列表
     * <p/>
     */
    protected void initQueueManager() {
        this.queueManager.init();
    }

    /**
     * <p>
     * 初始化定时任务管理器，稍后在任务调度器中会使用到此组件初始化之后的数据。
     * <p/>
     * <p>
     * 数据包括：<br/>
     * 1、一个TaskExecutor列表
     * <p/>
     */
    protected void initScheduleManager() {
        this.scheduleManager.init();
    }

    /**
     * <p>
     * 合并队列任务管理器和定时任务管理器中初始化好的TaskExecutor列表,并加载到任务调度器中，再初始化任务调度器。
     * <p/>
     */
    protected void initTaskWorker() {
        this.taskWorker.init();
    }
}
