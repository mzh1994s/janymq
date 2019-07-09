package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.annotation.Loopline;
import cn.mzhong.janytask.annotation.Pipleline;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.exception.TaskNotFoundException;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.QueueManager;

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
public class TaskProducerFactory {

    public static Object newInstance(TaskContext context, Class<?> _class) {
        return Proxy.newProxyInstance(
                _class.getClassLoader(),
                new Class[]{_class},
                new ProducerInvocationHandler(context));
    }
}

class ProducerInvocationHandler implements InvocationHandler {
    TaskContext context;
    Map<Method, QueueManager> lineManagerMap;

    ProducerInvocationHandler(TaskContext context) {
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

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!isLineMethod(method)) {
            return method.invoke(proxy, args);
        }
        QueueManager storeManager = lineManagerMap.get(method);
        if (storeManager == null) {
            throw new TaskNotFoundException("此方法不能作为提供者使用：" + method.getName());
        }
        Message message = new Message();
        message.setContent(args);
        storeManager.push(message);
        return false;
    }
}
