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

public class LoopLineProcessor implements TaskAnnotationProcessor<Loopline, LooplineInfo> {

    Logger Log = LoggerFactory.getLogger(LoopLineProcessor.class);

    public Class<Loopline> getAnnotationClass() {
        return Loopline.class;
    }

    public void processConsumer(TaskContext context, ConsumerInfo<Loopline> consumerInfo) {
        LooplineInfo looplineInfo = new LooplineInfo(
                consumerInfo.getProducerClass(),
                consumerInfo.getProducerMethod(),
                consumerInfo.getConsumerClass(),
                consumerInfo.getConsumerMethod(),
                consumerInfo.getAnnotation());
        // 注册messageDao
        MessageDao messageDao = context.getQueueProvider().createMessageDao(looplineInfo);
        consumerInfo.setMessageDao(messageDao);
        context.getConsumerExecutorService().execute(
                new TaskExecutor<Loopline>(context, this, consumerInfo));
    }

    public void processProducer(TaskContext context, ProducerInfo<Loopline> producerInfo) {
        Map<Method, MessageDao> methodMessageDaoMap = context.getMethodMessageDaoMap();
        LooplineInfo looplineInfo = new LooplineInfo(
                producerInfo.getProducerClass(),
                producerInfo.getProducerMethod(),
                null,
                null,
                producerInfo.getAnnotation());

        // 返回值判断
        Method method = producerInfo.getProducerMethod();
        Class<?> returnType = method.getReturnType();
        if (returnType != Boolean.class && returnType != boolean.class) {
            throw new RuntimeException("环线" + looplineInfo.ID() + "对应的方法" + method.getName() + "返回值应为Boolean");
        }

        // 注册messageDao
        MessageDao messageDao = context.getQueueProvider().createMessageDao(looplineInfo);
        producerInfo.setMessageDao(messageDao);

        // 映射Producer的MessageDao
        methodMessageDaoMap.put(producerInfo.getProducerMethod(), messageDao);
    }

    public void processMessage(Message message, ConsumerInfo consumerInfo) {
        Object consumer = consumerInfo.getConsumer();
        Method method = consumerInfo.getConsumerMethod();
        MessageDao messageDao = consumerInfo.getMessageDao();
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
