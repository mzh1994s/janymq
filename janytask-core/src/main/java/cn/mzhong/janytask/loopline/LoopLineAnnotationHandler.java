package cn.mzhong.janytask.loopline;

import cn.mzhong.janytask.core.TaskAnnotationHandler;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class LoopLineAnnotationHandler implements TaskAnnotationHandler<Loopline> {

    public Class<Loopline> getAnnotationClass() {
        return Loopline.class;
    }

    public void handleProducer(TaskContext context, QueueInfo<Loopline> queueInfo) {
        // 返回值判断
        Method method = queueInfo.getProducerMethod();
        Class<?> returnType = method.getReturnType();
        if (returnType != Boolean.class && returnType != boolean.class) {
            throw new RuntimeException("环线" + queueInfo.ID() + "对应的方法" + method.getName() + "返回值应为Boolean");
        }
    }

    public TaskExecutor<Loopline> handleConsumer(TaskContext context, QueueInfo<Loopline> queueInfo) {
        return new LooplineTaskExecutor(context, queueInfo);
    }
}

class LooplineTaskExecutor extends TaskExecutor<Loopline> {

    Logger Log = LoggerFactory.getLogger(LoopLineAnnotationHandler.class);

    Loopline loopline;

    public LooplineTaskExecutor(TaskContext context, QueueInfo<Loopline> queueInfo) {
        super(context, queueInfo);
        this.loopline = queueInfo.getAnnotation();
        this.idleInterval = ValueUtils.uLong(loopline.idleInterval(), this.idleInterval);
        this.sleepInterval = ValueUtils.uLong(loopline.sleepInterval(), this.sleepInterval);
    }

    protected void invoke(Message message) {
        Object consumer = queueInfo.getConsumer();
        Method method = queueInfo.getConsumerMethod();
        MessageDao messageDao = queueInfo.getMessageDao();
        try {
            Boolean result = (Boolean) method.invoke(consumer, message.getContent());
            if (result != null && result) {
                messageDao.done(message);
            } else {
                messageDao.back(message);
            }
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            message.setThrowable(e);
            messageDao.error(message);
        }
    }
}
