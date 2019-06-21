package cn.mzhong.janymq.executor;

import java.util.concurrent.*;

public class MQExecutorService extends ThreadPoolExecutor {

    public MQExecutorService(String name) {
        super(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        super.setThreadFactory(new MQThreadFactory(name));
    }
}

class MQThreadFactory implements ThreadFactory {

    int cnt = 0;
    String name;

    MQThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, name + (cnt++));
    }
}
