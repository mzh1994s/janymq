package cn.mzhong.janytask.executor;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.tool.PInvoker;
import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;

import java.util.Date;

/**
 * 任务执行者，每个时间段只会有一个任务执行者在运行。
 *
 * @since 1.0.1
 */
public abstract class TaskExecutor implements Runnable {

    protected TaskContext context;
    protected Date next;
    protected JanyTask$CronSequenceGenerator cronSeq;
    protected PInvoker<TaskExecutor> onCompleteListener;

    public TaskExecutor(TaskContext context, JanyTask$CronSequenceGenerator cronSeq) {
        this.context = context;
        this.cronSeq = cronSeq;
        this.next = cronSeq.next(new Date());
    }

    public Date getNext() {
        return next;
    }

    public void setOnCompleteListener(PInvoker<TaskExecutor> onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
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

    protected void onComplete() {
        if (onCompleteListener != null) {
            try {
                onCompleteListener.invoke(this);
            } catch (Exception e) {
                // pass
            }
        }
    }

    protected abstract void execute();

    public void run() {
        this.sleep();
        if (this.context.isShutdown()) {
            return;
        }
        this.execute();
        this.next = cronSeq.next(next);
        this.onComplete();
    }
}
