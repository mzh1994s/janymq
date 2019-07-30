package cn.mzhong.janytask.application;

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

    private Application application;
    private TaskContext context;

    public TaskShutdownHook(Application application, TaskContext context) {
        this.application = application;
        this.context = context;
    }

    @Override
    public void run() {
        if (Log.isDebugEnabled()) {
            Log.debug("janytask application[" + application.getName() + "] Terminating...");
        }
        // 写终结
        context.setShutdown(true);
        // 执行所有的ShutdownRunnable
        Iterator<Runnable> iterator = context.getShutdownHooks().iterator();
        while (iterator.hasNext()) {
            iterator.next().run();
        }
        if (Log.isDebugEnabled()) {
            Log.debug("janytask application[" + application.getName() + "] terminated, bye bye!");
        }
    }
}
