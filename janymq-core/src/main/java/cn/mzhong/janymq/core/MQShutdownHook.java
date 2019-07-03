package cn.mzhong.janymq.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 正常终结、安全关闭消费者线程
 */
public class MQShutdownHook extends Thread {

    public MQShutdownHook(MQContext context) {
        super(new MQShutdownHookExecutor(context));
    }
}

class MQShutdownHookExecutor implements Runnable {
    final static Logger Log = LoggerFactory.getLogger(MQShutdownHookExecutor.class);
    MQContext context;
    ExecutorService executorService;

    public MQShutdownHookExecutor(MQContext context) {
        this.context = context;
        this.executorService = context.getConsumerExecutorService();
    }

    @Override
    public void run() {
        Log.debug("janymq应用程序正在终结...");
        // 写终结
        context.setShutdown(true);
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(1000, TimeUnit.SECONDS)) {
                Log.debug("janymq应用程序已终结，拜拜!");
                return;
            }
        } catch (InterruptedException e) {
            Log.debug(e.getLocalizedMessage(), e);
        }
        Log.debug("janymq应用程序可能未正常终结!");
    }
}
