package cn.mzhong.janytask.executor;

import cn.mzhong.janytask.core.TaskContext;

import java.util.Date;

/**
 * @since 1.0.1
 */
public abstract class TaskExecutor implements Runnable {

    protected TaskContext context;
    protected String cron = "* * * * * * ?";
    protected String zone;
    protected Date next = new Date();
    /**
     * 当前执行者状态
     *
     * @since 1.0.1
     */
    protected TaskExecutor.Status status = Status.READY;

    public TaskExecutor(TaskContext context) {
        this.context = context;
    }

    public Status getStatus() {
        return status;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isReady(){
        if(status == Status.READY && new Date().after(next)){
            return true;
        }
        return false;
    }

    protected abstract void execute();

    public void run() {
        if (context.isShutdown()) {
            return;
        }
        status = Status.BUSY;
        this.execute();
        status = Status.READY;
    }

    public enum Status {
        READY,
        BUSY
    }
}
