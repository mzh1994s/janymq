package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.core.TaskExecutor;
import cn.mzhong.janytask.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class QueueExecutor<A extends Annotation> extends TaskExecutor {

    final static Logger Log = LoggerFactory.getLogger(QueueExecutor.class);

    protected String ID;
    protected QueueInfo<A> queueInfo;
    protected MessageDao messageDao;
    protected Method method;
    protected Object consumer;
    protected QueueManager queueManager;
    protected long cnt = 0;

    public QueueExecutor(TaskContext context, QueueManager queueManager, QueueInfo<A> queueInfo) {
        super(context, new JanyTask$CronSequenceGenerator(
                ValueUtils.uEmptyStr(queueInfo.cron),
                ValueUtils.uEmptyStr(queueInfo.zone)
        ));
        this.queueManager = queueManager;
        this.queueInfo = queueInfo;
        this.messageDao = queueInfo.getMessageDao();
        this.ID = messageDao.ID();
        this.method = queueInfo.getConsumerMethod();
        this.consumer = queueInfo.getConsumer();
    }

    protected abstract void invoke(Message message);

    public void execute() {
        cnt++;
        try {
            invoke();
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            sleep(15000);
        }
    }

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
        int spendTime = (int) (System.currentTimeMillis() - startTimeMillis);
        if (Log.isDebugEnabled()) {
            int speed = 0;
            float time = spendTime + 1;
            int seconds = Math.round(time / 1000);
            if (done > 0) {
                speed = (int) (time / done);
            }
            Log.debug("'{}'：第{}轮消息处理完毕，数量:{}，总耗时:{}秒，单条耗时:{}毫秒", ID, cnt, done, seconds, speed);
        }
    }

}
