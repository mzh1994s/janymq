package cn.mzhong.janytask.executor;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.org.springframework.CronSequenceGenerator;

import java.util.Date;

/**
 * @since 1.0.1
 */
public abstract class TaskExecutor implements Runnable {

    protected TaskContext context;
    protected Date next;
    protected CronSequenceGenerator cronSeq;
    /**
     * 当前执行者状态
     *
     * @since 1.0.1
     */
    protected volatile TaskExecutor.Status status = Status.READY;

    public TaskExecutor(TaskContext context, CronSequenceGenerator cronSeq) {
        this.context = context;
        this.cronSeq = cronSeq;
        this.next = cronSeq.next(new Date());
    }

    public boolean isReady() {
        if (status == Status.READY) {
            return true;
        }
        return false;
    }

    public void setBusy() {
        this.status = Status.BUSY;
    }

    private void sleep() {
        long current = System.currentTimeMillis();
        long timeout = next.getTime() - current;
        if (timeout > 0) {
            sleep(timeout);
        }
    }

    protected void sleep(long timeout) {
        try {
            synchronized (this) {
                this.wait(timeout);
            }
        } catch (InterruptedException e) {
            // pass
        }
    }

    protected abstract void execute();

    public void run() {
        this.status = Status.BUSY;
        this.sleep();
        if (this.context.isShutdown()) {
            return;
        }
        this.execute();
        this.next = cronSeq.next(next);
        this.status = Status.READY;
    }

    public enum Status {
        READY,
        BUSY
    }
}
