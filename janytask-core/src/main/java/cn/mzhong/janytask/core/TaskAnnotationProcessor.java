package cn.mzhong.janytask.core;

import cn.mzhong.janytask.consumer.QueueMethodInfo;
import cn.mzhong.janytask.producer.ProducerInfo;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.QueueInfo;

import java.lang.annotation.Annotation;

/**
 * Task注解处理器
 *
 * @param <A>
 */
public interface TaskAnnotationProcessor<A extends Annotation, Info extends QueueInfo> {

    Class<A> getAnnotationClass();

    void processProducer(TaskContext context, ProducerInfo<A> producerInfo);

    void processConsumer(TaskContext context, QueueMethodInfo<A> consumerInfo);

    void processMessage(Message message, QueueMethodInfo<A> consumerInfo);
}
