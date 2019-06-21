package cn.mzhong.janymq.initializer;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManagerProvider;
import cn.mzhong.janymq.line.LineManager;

import java.lang.reflect.Method;
import java.util.Map;

public class MQLineManagerInitializer implements MQComponentInitializer {

    @Override
    public void init(MQContext context) {
        LineManagerProvider builder = context.getLineManagerProvider();
        Map<String, LineManager> lineManagerMap = context.getLineManagerMap();
        for (Class<?> producerClass : context.getProducerClassSet()) {
            for (Method method : producerClass.getMethods()) {
                // 扫描流水线队列
                Pipleline piplelineAnnotation = method.getAnnotation(Pipleline.class);
                if (piplelineAnnotation != null) {
                    cn.mzhong.janymq.line.Pipleline pipleline =
                            new cn.mzhong.janymq.line.Pipleline(producerClass, method, piplelineAnnotation);
                    if (lineManagerMap.containsKey(pipleline.ID())) {
                        throw new RuntimeException("消费者端冲突" + pipleline.getValue() + "！");
                    }
                    if (method.getReturnType() != Void.TYPE) {
                        throw new RuntimeException("流水线" + pipleline.getValue() + "对应的方法" + method.getName() + "返回值应为void");
                    }
                    lineManagerMap.put(pipleline.ID(), builder.getPiplelineManager(context, pipleline));
                    continue;
                }
                // 扫描环线队列
                Loopline looplineAnnotation = method.getAnnotation(Loopline.class);
                if (looplineAnnotation != null) {
                    cn.mzhong.janymq.line.Loopline loopline =
                            new cn.mzhong.janymq.line.Loopline(producerClass, method, looplineAnnotation);
                    if (lineManagerMap.containsKey(loopline.ID())) {
                        throw new RuntimeException("消费者端冲突：" + loopline.getValue() + "！");
                    }
                    Class<?> returnType = method.getReturnType();
                    if (returnType != Boolean.class && returnType != boolean.class) {
                        throw new RuntimeException("环线" + loopline.getVersion() + "对应的方法" + method.getName() + "返回值应为Boolean");
                    }
                    lineManagerMap.put(loopline.ID(), builder.getLoopLineManager(context, loopline));
                    continue;
                }
            }
        }
    }
}
