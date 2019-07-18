package cn.mzhong.janytask.executor;

import cn.mzhong.janytask.core.TaskContext;

/**
 * @since 1.0.1
 */
public abstract class TaskExecutor implements Runnable {

    protected TaskContext context;

    public TaskExecutor(TaskContext context) {
        this.context = context;
    }

    /**
     * 当前执行者状态
     *
     * @since 1.0.1
     */
    protected TaskExecutor.Status status;

    public Status getStatus() {
        return status;
    }

    public enum Status {
        READY,
        BUSY
    }
}
