package cn.mzhong.janytask.executor;

import java.util.concurrent.*;

public class TaskExecutorService extends ThreadPoolExecutor {

    public TaskExecutorService(String name) {
        super(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        super.setThreadFactory(new TaskThreadFactory(name));
    }
}

class TaskThreadFactory implements ThreadFactory {

    int cnt = 0;
    String name;

    TaskThreadFactory(String name) {
        this.name = name;
    }

    public Thread newThread(Runnable r) {
        return new Thread(r, name + (cnt++));
    }
}
