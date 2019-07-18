package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;

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
public class ProducerFactory {

    public static Object newInstance(Map<Method, MessageDao> methodMessageDaoMap, Class<?> _class) {
        return Proxy.newProxyInstance(
                _class.getClassLoader(),
                new Class[]{_class},
                new ProducerInvocationHandler(methodMessageDaoMap));
    }
}

class ProducerInvocationHandler implements InvocationHandler {
    Map<Method, MessageDao> methodMessageDaoMap;

    ProducerInvocationHandler(Map<Method, MessageDao> methodMessageDaoMap) {
        this.methodMessageDaoMap = methodMessageDaoMap;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MessageDao messageDao = methodMessageDaoMap.get(method);
        if (messageDao == null) {
            return method.invoke(proxy, args);
        }
        Message message = new Message();
        message.setContent(args);
        messageDao.push(message);
        return false;
    }
}
