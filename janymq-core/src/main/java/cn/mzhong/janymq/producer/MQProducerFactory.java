package cn.mzhong.janymq.producer;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.exception.MQNotFoundException;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.line.LineManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 使用工厂方式创建消费者代理。
 *
 * @author mzhong
 * @since 1.0.0
 */
public class MQProducerFactory {

    public static Object newInstance(MQContext context, Class<?> _class) {
        return Proxy.newProxyInstance(
                _class.getClassLoader(),
                new Class[]{_class},
                new ProducerInvocationHandler(context));
    }
}

class ProducerInvocationHandler implements InvocationHandler {
    MQContext context;
    Map<Method, LineManager> lineManagerMap;

    ProducerInvocationHandler(MQContext context) {
        this.context = context;
        lineManagerMap = context.getMethodLineManagerMap();
    }

    /**
     * 只允许Object以外的方法进行代理
     *
     * @param method
     * @return
     */
    static boolean isLineMethod(Method method) {
        boolean isPipleline = method.getAnnotation(Pipleline.class) != null;
        boolean isLoopline = method.getAnnotation(Loopline.class) != null;
        return isPipleline || isLoopline;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!isLineMethod(method)) {
            return method.invoke(proxy, args);
        }
        LineManager storeManager = lineManagerMap.get(method);
        if (storeManager == null) {
            throw new MQNotFoundException("此方法不能作为提供者使用：" + method.getName());
        }
        Message message = new Message();
        message.setData(args);
        storeManager.push(message);
        return false;
    }
}
