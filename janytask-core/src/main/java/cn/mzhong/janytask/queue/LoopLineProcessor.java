package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.consumer.ConsumerInfo;
import cn.mzhong.janytask.core.TaskAnnotationProcessor;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.executor.TaskLooplineExecutor;
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
        context.getConsumerExecutorService().execute(
                new TaskLooplineExecutor(
                        context,
                        consumerInfo.getConsumer(),
                        consumerInfo.getConsumerMethod(),
                        new LooplineInfo(
                                consumerInfo.getProducerClass(),
                                consumerInfo.getProducerMethod(),
                                consumerInfo.getConsumerClass(),
                                consumerInfo.getConsumerMethod(),
                                consumerInfo.getAnnotation())));
    }

    public void processProducer(TaskContext context, ProducerInfo<Loopline> producerInfo) {
        Map<Method, MessageDao> methodQueueManagerMap = context.getMethodQueueManagerMap();
        LooplineInfo looplineInfo = new LooplineInfo(
                producerInfo.getProducerClass(),
                producerInfo.getProducerMethod(),
                null,
                null,
                producerInfo.getAnnotation());
        Method method = producerInfo.getProducerMethod();
        if (methodQueueManagerMap.containsKey(method)) {
            throw new RuntimeException("列表冲突：" + looplineInfo.ID() + "！");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType != Boolean.class && returnType != boolean.class) {
            throw new RuntimeException("环线" + looplineInfo.ID() + "对应的方法" + method.getName() + "返回值应为Boolean");
        }
        context.getProducerMap().put(
                producerInfo.getProducerClass(),
                context.getQueueProvider().createMessageDao(looplineInfo));
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
