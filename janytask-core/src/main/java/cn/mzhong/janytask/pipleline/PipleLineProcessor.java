package cn.mzhong.janytask.pipleline;

import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class PipleLineProcessor implements TaskAnnotationProcessor<Pipleline> {


    public Class<Pipleline> getAnnotationClass() {
        return Pipleline.class;
    }

    public void processProducer(TaskContext context, QueueInfo<Pipleline> queueInfo) {
        Method method = queueInfo.getProducerMethod();
        if (method.getReturnType() != Void.TYPE) {
            throw new RuntimeException("流水线" + queueInfo.ID() + "对应的方法" + method.getName() + "返回值应为void");
        }
    }

    public TaskExecutor<Pipleline> processConsumer(TaskContext context, QueueInfo<Pipleline> queueInfo) {
        return new PiplelineTaskExecutor(context, queueInfo);
    }
}

class PiplelineTaskExecutor extends TaskExecutor<Pipleline> {

    Pipleline pipleline;

    Logger Log = LoggerFactory.getLogger(PipleLineProcessor.class);

    public PiplelineTaskExecutor(TaskContext context, QueueInfo<Pipleline> queueInfo) {
        super(context, queueInfo);
        this.pipleline = queueInfo.getAnnotation();
        this.idleInterval = ValueUtils.uLong(pipleline.idleInterval(), this.idleInterval);
        this.sleepInterval = ValueUtils.uLong(pipleline.sleepInterval(), this.sleepInterval);
    }

    protected void invoke(Message message) {
        Object consumer = queueInfo.getConsumer();
        Method method = queueInfo.getConsumerMethod();
        MessageDao messageDao = queueInfo.getMessageDao();
        try {
            method.invoke(consumer, message.getContent());
            messageDao.done(message);
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            message.setThrowable(e);
            messageDao.error(message);
        }
    }
}