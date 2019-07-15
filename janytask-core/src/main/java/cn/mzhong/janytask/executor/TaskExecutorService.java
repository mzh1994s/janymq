package cn.mzhong.janytask.executor;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutorService extends ThreadPoolExecutor {

    public TaskExecutorService(int nThreads) {
        super(nThreads,
                nThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                new TaskThreadFactory("janytask-executor"));
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
