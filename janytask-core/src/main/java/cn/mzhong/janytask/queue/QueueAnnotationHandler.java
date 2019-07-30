package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContext;

import java.lang.annotation.Annotation;

/**
 * TaskQueue注解处理器
 *
 * @param <A>
 */
public interface QueueAnnotationHandler<A extends Annotation> {

    Class<A> getAnnotationClass();

    void handleProducer(TaskContext context, QueueManager queueManager, QueueInfo<A> queueInfo);

    QueueExecutor<A> handleConsumer(TaskContext context, QueueManager queueManager, QueueInfo<A> queueInfo);
}
