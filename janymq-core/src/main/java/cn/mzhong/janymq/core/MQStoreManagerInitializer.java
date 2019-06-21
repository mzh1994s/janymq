package cn.mzhong.janymq.core;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.line.LineManagerProvider;
import cn.mzhong.janymq.line.LineIDGenerator;
import cn.mzhong.janymq.line.LineManager;

import java.lang.reflect.Method;
import java.util.Map;

public class MQStoreManagerInitializer implements MQComponentInitializer {

    @Override
    public void init(MQContext context) {
        LineManagerProvider builder = context.getLineManagerProvider();
        Map<String, LineManager> storeManagerMap = context.getLineManagerMap();
        for (Class<?> producerClass : context.getProducerClassSet()) {
            for (Method method : producerClass.getMethods()) {
                // 扫描流水线队列
                Pipleline pipleline = method.getAnnotation(Pipleline.class);
                if (pipleline != null) {
                    String lineId = LineIDGenerator.generate(pipleline);
                    if (storeManagerMap.containsKey(lineId)) {
                        throw new RuntimeException("消费者端冲突" + pipleline.value() + "！");
                    }
                    if (method.getReturnType() != Void.TYPE) {
                        throw new RuntimeException("流水线" + pipleline.value() + "对应的方法" + method.getName() + "返回值应为void");
                    }
                    storeManagerMap.put(lineId, builder.getPiplelineManager(context, pipleline));
                    continue;
                }
                // 扫描环线队列
                Loopline loopline = method.getAnnotation(Loopline.class);
                if (loopline != null) {
                    String lineId = LineIDGenerator.generate(loopline);
                    if (storeManagerMap.containsKey(lineId)) {
                        throw new RuntimeException("消费者端冲突：" + pipleline.value() + "！");
                    }
                    Class<?> returnType = method.getReturnType();
                    if (returnType != Boolean.class && returnType != boolean.class) {
                        throw new RuntimeException("环线" + loopline.value() + "对应的方法" + method.getName() + "返回值应为Boolean");
                    }
                    storeManagerMap.put(lineId, builder.getLoopLineManager(context, loopline));
                    continue;
                }
            }
        }
    }
}
