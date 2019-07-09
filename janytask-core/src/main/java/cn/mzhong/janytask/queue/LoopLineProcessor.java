package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

import java.lang.reflect.Method;
import java.util.Map;

public class LoopLineProcessor implements QueueAnnotationProcessor<Loopline> {
    public Class<Loopline> getAnnotationClass() {
        return Loopline.class;
    }

    public void invoke(TaskContext context, Class producerClass, Method method, Loopline loopline) {
        Map<Method, QueueManager> methodQueueManagerMap = context.getMethodQueueManagerMap();
        LooplineInfo looplineInfo = new LooplineInfo(producerClass, method, loopline);
        if (methodQueueManagerMap.containsKey(method)) {
            throw new RuntimeException("列表冲突：" + looplineInfo.ID() + "！");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType != Boolean.class && returnType != boolean.class) {
            throw new RuntimeException("环线" + looplineInfo.ID() + "对应的方法" + method.getName() + "返回值应为Boolean");
        }
        methodQueueManagerMap.put(method, context.getQueueProvider().getlooplinemanager(looplineInfo));
    }
}
