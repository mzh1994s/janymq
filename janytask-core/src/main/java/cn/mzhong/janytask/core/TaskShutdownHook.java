package cn.mzhong.janytask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 正常终结、安全关闭消费者线程
 */
public class TaskShutdownHook extends Thread {

    public TaskShutdownHook(TaskContext context) {
        super(new TaskShutdownHookExecutor(context));
    }
}

class TaskShutdownHookExecutor implements Runnable {
    final static Logger Log = LoggerFactory.getLogger(TaskShutdownHookExecutor.class);
    TaskContext context;

    public TaskShutdownHookExecutor(TaskContext context) {
        this.context = context;
    }

    public void run() {
        Log.debug("janytask应用程序正在终结...");
        // 写终结
        context.setShutdown(true);
        try {
            if (context.getTaskWorker().shutdownAndAwait()) {
                Log.debug("janytask应用程序已终结，拜拜!");
                return;
            }
        } catch (InterruptedException e) {
            Log.debug(e.getLocalizedMessage(), e);
        }
        Log.debug("janytask应用程序可能未正常终结!");
    }
}
