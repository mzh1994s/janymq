package cn.mzhong.janytask.executor;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class TaskExecutor<A extends Annotation> implements Runnable {

    final static Logger Log = LoggerFactory.getLogger(TaskExecutor.class);

    protected String ID;
    protected QueueInfo<A> queueInfo;
    protected TaskContext context;
    protected MessageDao messageDao;
    protected Method method;
    protected Object consumer;
    protected long idleInterval;
    protected long sleepInterval;
    protected long cnt = 0;

    public TaskExecutor(TaskContext context, QueueInfo<A> queueInfo) {
        this.context = context;
        this.queueInfo = queueInfo;
        this.messageDao = queueInfo.getMessageDao();
        this.ID = messageDao.ID();
        this.method = queueInfo.getConsumerMethod();
        this.consumer = queueInfo.getConsumer();
        this.idleInterval = context.getQueueConfig().getIdleInterval();
        this.sleepInterval = context.getQueueConfig().getSleepInterval();
    }

    protected abstract void invoke(Message message);

    protected void invoke() {
        // 获取当前列表长度，以当前列表长度作为空闲周期
        long length = messageDao.length();
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
            Message message = messageDao.poll();
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
