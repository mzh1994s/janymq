package cn.mzhong.janymq.core;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.exception.MQNotFoundException;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.line.LineIDGenerator;
import cn.mzhong.janymq.line.LineManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用工厂方式创建消费者代理。
 *
 * @author mzhong
 * @since 1.0.0
 */
public class MQProducerFactory {

    public static Object newInstance(MQContext context, Class<?> _class) {
        return Proxy.newProxyInstance(_class.getClassLoader(), new Class[]{_class}, new ProducerInvocationHandler(context));
    }
}

class ProducerInvocationHandler implements InvocationHandler {
    MQContext context;
    Map<Method, LineManager> storeManagerMap = new HashMap<>();

    ProducerInvocationHandler(MQContext context) {
        this.context = context;
    }

    synchronized LineManager getStoreManager(Method method) {
        LineManager storeManager = storeManagerMap.get(method);
        if (storeManager == null) {
            String lineId = null;
            Pipleline pipleline = method.getAnnotation(Pipleline.class);
            if (pipleline != null) {
                lineId = LineIDGenerator.generate(pipleline);
            } else {
                Loopline loopline = method.getAnnotation(Loopline.class);
                if (loopline != null) {
                    lineId = LineIDGenerator.generate(loopline);
                }
            }
            if (lineId != null) {
                storeManager = context.getLineManagerMap().get(lineId);
                storeManagerMap.put(method, storeManager);
            }
        }
        return storeManager;
    }

    /**
     * 只允许Object以外的方法进行代理
     *
     * @param method
     * @return
     */
    private boolean methodAccess(Method method) {
        boolean piplelineAccess = method.getAnnotation(Pipleline.class) != null;
        boolean looplineAccess = method.getAnnotation(Loopline.class) != null;
        return piplelineAccess || looplineAccess;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!methodAccess(method)) {
            return false;
        }
        LineManager storeManager = getStoreManager(method);
        if (storeManager == null) {
            throw new MQNotFoundException("此方法不能作为提供者使用：" + method.getName());
        }
        Message message = new Message();
        message.setData(args);
        storeManager.push(message);
        return false;
    }
}
