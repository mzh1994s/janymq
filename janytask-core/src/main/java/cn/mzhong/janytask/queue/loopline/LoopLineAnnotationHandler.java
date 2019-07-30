package cn.mzhong.janytask.queue.loopline;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class LoopLineAnnotationHandler implements QueueAnnotationHandler<Loopline> {

    public Class<Loopline> getAnnotationClass() {
        return Loopline.class;
    }

    public void handleProducer(TaskContext context, QueueManager queueManager, QueueInfo<Loopline> queueInfo) {
        // 返回值判断
        Method method = queueInfo.getProducerMethod();
        Class<?> returnType = method.getReturnType();
        if (returnType != Boolean.class && returnType != boolean.class) {
            throw new RuntimeException("环线" + queueInfo.getId() + "对应的方法" + method.getName() + "返回值应为Boolean");
        }
    }

    public QueueExecutor<Loopline> handleConsumer(TaskContext context, QueueManager queueManager, QueueInfo<Loopline> queueInfo) {
        return new LooplineTaskExecutor(context, queueManager, queueInfo);
    }
}

class LooplineTaskExecutor extends QueueExecutor<Loopline> {

    Logger Log = LoggerFactory.getLogger(LoopLineAnnotationHandler.class);

    Loopline loopline;

    public LooplineTaskExecutor(TaskContext context, QueueManager queueManager, QueueInfo<Loopline> queueInfo) {
        super(context, queueManager, queueInfo);
        this.loopline = queueInfo.getAnnotation();
    }

    protected void invoke(Message message) {
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
