package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.consumer.ConsumerInfo;
import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.executor.TaskPiplelineExecutor;
import cn.mzhong.janytask.producer.ProducerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class PipleLineProcessor implements TaskAnnotationProcessor<Pipleline, PiplelineInfo> {

    Logger Log = LoggerFactory.getLogger(PipleLineProcessor.class);
    
    public Class<Pipleline> getAnnotationClass() {
        return Pipleline.class;
    }

    public void processConsumer(TaskContext context, ConsumerInfo<Pipleline> consumerInfo) {
        context.getConsumerExecutorService().execute(
                new TaskPiplelineExecutor(
                        context,
                        consumerInfo.getConsumer(),
                        consumerInfo.getConsumerMethod(),
                        new PiplelineInfo(
                                consumerInfo.getProducerClass(),
                                consumerInfo.getProducerMethod(),
                                consumerInfo.getConsumerClass(),
                                consumerInfo.getConsumerMethod(),
                                consumerInfo.getAnnotation())));
    }

    public void processProducer(TaskContext context, ProducerInfo<Pipleline> producerInfo) {
        Map<Method, MessageDao> methodQueueManagerMap = context.getMethodQueueManagerMap();
        PiplelineInfo piplelineInfo = new PiplelineInfo(
                producerInfo.getProducerClass(),
                producerInfo.getProducerMethod(),
                null,
                null,
                producerInfo.getAnnotation());
        Method method = producerInfo.getProducerMethod();
        if (methodQueueManagerMap.containsKey(method)) {
            throw new RuntimeException("列表冲突" + piplelineInfo.ID() + "！");
        }
        if (method.getReturnType() != Void.TYPE) {
            throw new RuntimeException("流水线" + piplelineInfo.ID() + "对应的方法" + method.getName() + "返回值应为void");
        }
        methodQueueManagerMap.put(method, context.getQueueProvider().createMessageDao(piplelineInfo));
    }

    public void processMessage(Message message, ConsumerInfo consumerInfo) {
        Object consumer = consumerInfo.getConsumer();
        Method method = consumerInfo.getConsumerMethod();
        MessageDao messageDao = consumerInfo.getMessageDao();
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
