package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.consumer.ConsumerInfo;
import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.executor.TaskExecutor;
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
        PiplelineInfo piplelineInfo = new PiplelineInfo(
                consumerInfo.getProducerClass(),
                consumerInfo.getProducerMethod(),
                consumerInfo.getConsumerClass(),
                consumerInfo.getConsumerMethod(),
                consumerInfo.getAnnotation());
        // 注册messageDao
        MessageDao messageDao = context.getQueueProvider().createMessageDao(piplelineInfo);
        consumerInfo.setMessageDao(messageDao);
        context.getConsumerExecutorService().execute(
                new TaskExecutor<Pipleline>(context, this, consumerInfo));
    }

    public void processProducer(TaskContext context, ProducerInfo<Pipleline> producerInfo) {
        Map<Method, MessageDao> methodMessageDaoMap = context.getMethodMessageDaoMap();
        PiplelineInfo piplelineInfo = new PiplelineInfo(
                producerInfo.getProducerClass(),
                producerInfo.getProducerMethod(),
                null,
                null,
                producerInfo.getAnnotation());

        Method method = producerInfo.getProducerMethod();
        if (method.getReturnType() != Void.TYPE) {
            throw new RuntimeException("流水线" + piplelineInfo.ID() + "对应的方法" + method.getName() + "返回值应为void");
        }

        // 注册messageDao
        MessageDao messageDao = context.getQueueProvider().createMessageDao(piplelineInfo);
        producerInfo.setMessageDao(messageDao);

        // 映射Producer的MessageDao
        methodMessageDaoMap.put(producerInfo.getProducerMethod(), messageDao);
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
