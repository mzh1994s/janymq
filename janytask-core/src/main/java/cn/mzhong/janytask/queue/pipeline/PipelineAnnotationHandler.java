package cn.mzhong.janytask.queue.pipeline;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.*;
import cn.mzhong.janytask.queue.ack.Ack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class PipelineAnnotationHandler implements QueueAnnotationHandler<Pipeline> {


    public Class<Pipeline> getAnnotationClass() {
        return Pipeline.class;
    }

    public void handleProducer(TaskContext context, QueueManager queueManager, QueueInfo<Pipeline> queueInfo) {
        Method method = queueInfo.getProducerMethod();
        Class<?> returnType = method.getReturnType();
        if (returnType != Void.TYPE && !Ack.class.isAssignableFrom(returnType)) {
            throw new RuntimeException("流水线" + queueInfo.getId() + "对应的方法" + method.getName() + "返回值应为void或者为AckChannel");
        }
    }

    public QueueTaskExecutor<Pipeline> handleConsumer(TaskContext context, QueueManager queueManager, QueueInfo<Pipeline> queueInfo) {
        return new PipelineTaskExecutor(context, queueManager, queueInfo);
    }
}

class PipelineTaskExecutor extends QueueTaskExecutor<Pipeline> {

    Pipeline pipeline;

    Logger Log = LoggerFactory.getLogger(PipelineAnnotationHandler.class);

    public PipelineTaskExecutor(TaskContext context, QueueManager queueManager, QueueInfo<Pipeline> queueInfo) {
        super(context, queueManager, queueInfo);
        this.pipeline = queueInfo.getAnnotation();
    }

    protected void invoke(Message message) {
        try {
            // 执行消费者方法
            Object result = method.invoke(consumer, message.getArgs());
            // 如果消费者方法返回Back请求，则将消息返回给队列
            if (result == Ack.BACK) {
                messageDao.back(message);
            }
            // 如果消费者方法返回的是一个Ack信号，则取Ack中的结果
            else if (Ack.class.isAssignableFrom(method.getReturnType())) {
                if (result != null) {
                    Ack ack = (Ack) result;
                    message.setResult(ack.get());
                }
                messageDao.done(message);
            }
            // 否则其他返回值（应该不存在了，在初始化消费者的时候会检测返回值的正确性），
            // 则将消息设置为完成状态
            else {
                messageDao.done(message);
            }
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            message.setThrowable(e);
            messageDao.error(message);
        }
    }
}