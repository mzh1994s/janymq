package cn.mzhong.janytask.core;

import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.queue.QueueInfo;

import java.lang.annotation.Annotation;

/**
 * Task注解处理器
 *
 * @param <A>
 */
public interface TaskAnnotationProcessor<A extends Annotation> {

    Class<A> getAnnotationClass();

    void processProducer(TaskContext context, QueueInfo<A> queueInfo);

    TaskExecutor<A> processConsumer(TaskContext context, QueueInfo<A> queueInfo);
}
