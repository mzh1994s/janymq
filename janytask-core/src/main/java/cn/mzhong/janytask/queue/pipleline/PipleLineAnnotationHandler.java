package cn.mzhong.janytask.queue.pipleline;

import cn.mzhong.janytask.queue.*;
import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.ack.Ack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class PipleLineAnnotationHandler implements QueueAnnotationHandler<Pipleline> {


    public Class<Pipleline> getAnnotationClass() {
        return Pipleline.class;
    }

    public void handleProducer(TaskContext context, QueueManager queueManager, QueueInfo<Pipleline> queueInfo) {
        Method method = queueInfo.getProducerMethod();
        Class<?> returnType = method.getReturnType();
        if (returnType != Void.TYPE && !Ack.class.isAssignableFrom(returnType)) {
            throw new RuntimeException("流水线" + queueInfo.getId() + "对应的方法" + method.getName() + "返回值应为void或者为AckChannel");
        }
    }

    public QueueExecutor<Pipleline> handleConsumer(TaskContext context, QueueManager queueManager, QueueInfo<Pipleline> queueInfo) {
        return new PiplelineTaskExecutor(context, queueManager, queueInfo);
    }
}

class PiplelineTaskExecutor extends QueueExecutor<Pipleline> {

    Pipleline pipleline;

    Logger Log = LoggerFactory.getLogger(PipleLineAnnotationHandler.class);

    public PiplelineTaskExecutor(TaskContext context, QueueManager queueManager, QueueInfo<Pipleline> queueInfo) {
        super(context, queueManager, queueInfo);
        this.pipleline = queueInfo.getAnnotation();
    }

    protected void invoke(Message message) {
        try {
            Object invoke = method.invoke(consumer, message.getArgs());
            if (Ack.class.isAssignableFrom(method.getReturnType())) {
                if (invoke != null) {
                    Ack ackChannel = (Ack) invoke;
                    message.setResult(ackChannel.get());
                }
            }
            messageDao.done(message);
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            message.setThrowable(e);
            messageDao.error(message);
        }
    }
}