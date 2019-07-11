package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;

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

    public static Object newInstance(TaskContext context, Class<?> _class) {
        return Proxy.newProxyInstance(
                _class.getClassLoader(),
                new Class[]{_class},
                new ProducerInvocationHandler(context));
    }
}

class ProducerInvocationHandler implements InvocationHandler {
    TaskContext context;

    ProducerInvocationHandler(TaskContext context) {
        this.context = context;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MessageDao messageDao = context.getMethodMessageDaoMap().get(method);
        if (messageDao == null) {
            return method.invoke(proxy, args);
        }
        Message message = new Message();
        message.setContent(args);
        messageDao.push(message);
        return false;
    }
}
