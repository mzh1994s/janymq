package cn.mzhong.janymq.executor;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public abstract class MQLineExecutor implements Runnable {
    final static Logger Log = LoggerFactory.getLogger(MQLineExecutor.class);

    protected MQContext context;
    protected LineManager lineManager;
    protected String lineId;
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
        this.lineId = lineManager.lineId();
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
            Log.debug("'{}'：第{}轮消息处理开始, 本次目标长度:{}", lineId, cnt, length);
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
            long time = System.currentTimeMillis() - startTimeMillis;
            int seconds = (int) (time / 1000);
            int speed = (int) (done / (seconds == 0 ? 1 : seconds));
            Log.debug("'{}'：第{}轮消息处理完毕，数量:{}，耗时:{}秒，速度:{}消息/秒", lineId, cnt, done, seconds, speed);
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
