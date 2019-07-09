package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Queue注解处理器
 *
 * @param <A>
 */
public interface QueueAnnotationProcessor<A extends Annotation> {

    Class<A> getAnnotationClass();

    void invoke(TaskContext context, Class<?> producerClass, Method method, A a);

}
