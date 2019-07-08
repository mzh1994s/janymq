package cn.mzhong.janytask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
    ExecutorService executorService;

    public TaskShutdownHookExecutor(TaskContext context) {
        this.context = context;
        this.executorService = context.getConsumerExecutorService();
    }

    public void run() {
        Log.debug("janytask应用程序正在终结...");
        // 写终结
        context.setShutdown(true);
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(1000, TimeUnit.SECONDS)) {
                Log.debug("janytask应用程序已终结，拜拜!");
                return;
            }
        } catch (InterruptedException e) {
            Log.debug(e.getLocalizedMessage(), e);
        }
        Log.debug("janytask应用程序可能未正常终结!");
    }
}
