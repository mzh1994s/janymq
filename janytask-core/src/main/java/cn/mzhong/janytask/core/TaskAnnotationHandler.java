package cn.mzhong.janytask.core;

import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.queue.QueueInfo;

import java.lang.annotation.Annotation;

/**
 * Task注解处理器
 *
 * @param <A>
 */
public interface TaskAnnotationHandler<A extends Annotation> {

    Class<A> getAnnotationClass();

    void handleProducer(TaskContext context, QueueInfo<A> queueInfo);

    TaskExecutor<A> handleConsumer(TaskContext context, QueueInfo<A> queueInfo);
}
