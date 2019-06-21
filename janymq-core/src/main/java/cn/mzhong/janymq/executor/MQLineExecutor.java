package cn.mzhong.janymq.executor;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.NumberFormat;

public abstract class MQLineExecutor implements Runnable {
    final static Logger Log = LoggerFactory.getLogger(MQLineExecutor.class);

    protected MQContext context;
    protected LineManager lineManager;
    protected String ID;
    protected Method method;
    protected Object consumer;
    protected long idleInterval;
    protected long sleepInterval;
    protected long cnt = 0;

    public MQLineExecutor(MQContext context,
                          LineManager lineManager,
                          Method method,
                          Object consumer,
                          long idleInterval,
                          long sleepInterval) {
        this.context = context;
        this.lineManager = lineManager;
        this.ID = lineManager.ID();
        this.method = method;
        this.consumer = consumer;
        this.idleInterval = idleInterval;
        this.sleepInterval = sleepInterval;
    }

    abstract void invoke(Message message);

    protected void invoke() {
        // 获取当前列表长度，以当前列表长度作为空闲周期
        long length = lineManager.length();
        long startTimeMillis = 0;
        if (Log.isDebugEnabled()) {
            Log.debug("'{}'：第{}轮消息处理开始, 本次目标长度:{}", ID, cnt, length);
            startTimeMillis = System.currentTimeMillis();
        }
        long done = 0;
        while (done < length) {
            if (context.isShutdown()) {
                break;
            }
            Message message = lineManager.poll();
            if (message != null) {
                invoke(message);
            } else {
                break;
            }
            done++;
        }
        if (Log.isDebugEnabled()) {
            int speed = 0;
            int time = (int) (System.currentTimeMillis() - startTimeMillis + 1);
            int seconds = Math.round(time / 1000);
            if (done > 0) {
                speed = (int) (time / done);
            }
            Log.debug("'{}'：第{}轮消息处理完毕，数量:{}，总耗时:{}秒，单条耗时:{}毫秒", ID, cnt, done, seconds, speed);
        }
        ThreadUtils.sleep(idleInterval);
    }

    public void run() {
        while (true) {
            cnt++;
            if (context.isShutdown()) {
                break;
            }
            try {
                invoke();
            } catch (Exception e) {
                Log.error(e.getLocalizedMessage(), e);
                ThreadUtils.sleep(15000);
            }
        }
    }
}
