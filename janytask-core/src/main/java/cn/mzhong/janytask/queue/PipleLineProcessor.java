package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

import java.lang.reflect.Method;
import java.util.Map;

public class PipleLineProcessor implements QueueAnnotationProcessor<Pipleline> {

    public Class<Pipleline> getAnnotationClass() {
        return Pipleline.class;
    }

    public void invoke(TaskContext context, Class<?> producerClass, Method method, Pipleline pipleline) {
        Map<Method, QueueManager> methodQueueManagerMap = context.getMethodQueueManagerMap();
        PiplelineInfo piplelineInfo = new PiplelineInfo(producerClass, method, pipleline);
        if (methodQueueManagerMap.containsKey(method)) {
            throw new RuntimeException("列表冲突" + piplelineInfo.ID() + "！");
        }
        if (method.getReturnType() != Void.TYPE) {
            throw new RuntimeException("流水线" + piplelineInfo.ID() + "对应的方法" + method.getName() + "返回值应为void");
        }
        methodQueueManagerMap.put(method, context.getQueueProvider().getPiplelineManager(piplelineInfo));
    }
}
