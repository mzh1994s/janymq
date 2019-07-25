package cn.mzhong.janytask.queue;

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

    public static Object newInstance(Map<Method, MessageDao> messageDaoMap, Class<?> _class) {
        return Proxy.newProxyInstance(
                _class.getClassLoader(),
                new Class[]{_class},
                new ProducerInvocationHandler(messageDaoMap));
    }
}

class ProducerInvocationHandler implements InvocationHandler {
    Map<Method, MessageDao> messageDaoMap;

    ProducerInvocationHandler(Map<Method, MessageDao> messageDaoMap) {
        this.messageDaoMap = messageDaoMap;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MessageDao messageDao = messageDaoMap.get(method);
        if (messageDao == null) {
            return method.invoke(proxy, args);
        }
        Message message = new Message();
        message.setContent(args);
        messageDao.push(message);
        return false;
    }
}
