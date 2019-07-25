package cn.mzhong.janytask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 正常终结、安全关闭消费者线程
 */
public class TaskShutdownHook extends Thread {
    final static Logger Log = LoggerFactory.getLogger(TaskShutdownHook.class);

    private Set<Runnable> runnables = new HashSet<Runnable>();
    private TaskContext context;

    public TaskShutdownHook(TaskContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.debug("janytask应用程序正在终结...");
        // 写终结
        context.setShutdown(true);
        // 执行所有的ShutdownRunnable
        Iterator<Runnable> iterator = runnables.iterator();
        while (iterator.hasNext()) {
            iterator.next().run();
        }
        Log.debug("janytask应用程序已终结，拜拜!");
    }

    public void add(Runnable runnable) {
        runnables.add(runnable);
    }
}
