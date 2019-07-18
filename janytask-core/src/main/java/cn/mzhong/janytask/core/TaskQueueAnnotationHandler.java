package cn.mzhong.janytask.core;

import cn.mzhong.janytask.queue.TaskQueueExecutor;
import cn.mzhong.janytask.queue.QueueInfo;

import java.lang.annotation.Annotation;

/**
 * TaskQueue注解处理器
 *
 * @param <A>
 */
public interface TaskQueueAnnotationHandler<A extends Annotation> {

    Class<A> getAnnotationClass();

    void handleProducer(TaskContext context, QueueInfo<A> queueInfo);

    TaskQueueExecutor<A> handleConsumer(TaskContext context, QueueInfo<A> queueInfo);
}
