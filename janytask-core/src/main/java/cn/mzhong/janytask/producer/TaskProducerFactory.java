package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.Loopline;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.Pipleline;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 使用工厂方式创建消费者代理。
 *
 * @author mzhong
 * @since 1.0.0
 */
public class TaskProducerFactory {

    public static Object newInstance(Class<?> _class, MessageDao messageDao) {
        return Proxy.newProxyInstance(
                _class.getClassLoader(),
                new Class[]{_class},
                new ProducerInvocationHandler(messageDao));
    }
}

class ProducerInvocationHandler implements InvocationHandler {
    MessageDao messageDao;

    ProducerInvocationHandler(MessageDao messageDao) {
        this.messageDao = messageDao;
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
        Message message = new Message();
        message.setContent(args);
        messageDao.push(message);
        return false;
    }
}
