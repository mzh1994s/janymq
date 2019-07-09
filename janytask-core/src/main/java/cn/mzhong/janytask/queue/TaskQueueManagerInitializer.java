package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.annotation.Loopline;
import cn.mzhong.janytask.annotation.Pipleline;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.initializer.TaskComponentInitializer;
import cn.mzhong.janytask.queue.QueueProvider;
import cn.mzhong.janytask.queue.QueueManager;
import cn.mzhong.janytask.queue.LooplineInfo;
import cn.mzhong.janytask.queue.PiplelineInfo;

import java.lang.reflect.Method;
import java.util.Map;

public class TaskQueueManagerInitializer implements TaskComponentInitializer {

    public void init(TaskContext context) {
        QueueProvider provider = context.getQueueProvider();
        provider.init(context);
        Map<Method, QueueManager> methodLineManagerMap = context.getMethodLineManagerMap();
        for (Class<?> producerClass : context.getProducerClassSet()) {
            for (Method method : producerClass.getMethods()) {
                // 扫描流水线队列
                Pipleline pipleline = method.getAnnotation(Pipleline.class);
                if (pipleline != null) {
                    PiplelineInfo piplelineInfo = new PiplelineInfo(producerClass, method, pipleline);
                    if (methodLineManagerMap.containsKey(method)) {
                        throw new RuntimeException("列表冲突" + piplelineInfo.ID() + "！");
                    }
                    if (method.getReturnType() != Void.TYPE) {
                        throw new RuntimeException("流水线" + piplelineInfo.ID() + "对应的方法" + method.getName() + "返回值应为void");
                    }
                    methodLineManagerMap.put(method, provider.getPiplelineManager(piplelineInfo));
                    continue;
                }
                // 扫描环线队列
                Loopline loopline = method.getAnnotation(Loopline.class);
                if (loopline != null) {
                    LooplineInfo looplineInfo = new LooplineInfo(producerClass, method, loopline);
                    if (methodLineManagerMap.containsKey(method)) {
                        throw new RuntimeException("列表冲突：" + looplineInfo.ID() + "！");
                    }
                    Class<?> returnType = method.getReturnType();
                    if (returnType != Boolean.class && returnType != boolean.class) {
                        throw new RuntimeException("环线" + looplineInfo.ID() + "对应的方法" + method.getName() + "返回值应为Boolean");
                    }
                    methodLineManagerMap.put(method, provider.getlooplinemanager(looplineInfo));
                    continue;
                }
            }
        }
    }
}
