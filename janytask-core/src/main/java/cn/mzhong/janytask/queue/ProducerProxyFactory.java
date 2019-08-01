package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.ack.Ack;
import cn.mzhong.janytask.queue.ack.AckHandler;
import cn.mzhong.janytask.queue.ack.AckListener;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 使用工厂方式创建消费者代理。
 *
 * @author mzhong
 * @since 1.0.0
 */
public class ProducerProxyFactory {

    public static Object newInstance(Class<?> _class, TaskContext context) {
        return Proxy.newProxyInstance(
                _class.getClassLoader(),
                new Class[]{_class},
                new ProducerInvocationHandler(context));
    }
}

class ProducerInvocationHandler implements InvocationHandler {

    private Map<Method, MessageDao> messageDaoMap;
    private AckHandler ackHandler;

    ProducerInvocationHandler(TaskContext context) {
        QueueManager queueManager = context.getApplication().getQueueManager();
        this.messageDaoMap = queueManager.getMessageDaoMap();
        this.ackHandler = queueManager.getAckHandler();
    }

    private Object handleAckChannel(final Message message, final MessageDao messageDao) {
        // 加入AckHandler
        this.ackHandler.add(message, messageDao);
        return new Ack<Serializable>(null) {

            private static final long serialVersionUID = -4662066970080391522L;

            public Ack<Serializable> addListener(AckListener<Serializable> listener) {
                ackHandler.addListener(message, listener);
                return this;
            }

            public Ack<Serializable> push() {
                messageDao.push(message);
                return this;
            }

            public Serializable get() throws InterruptedException, ExecutionException {
                return ackHandler.get(message);
            }

            public Serializable get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
                return ackHandler.get(message, timeout, unit);
            }
        };
    }

    private boolean handleBoolean(Message message, MessageDao messageDao) {
        messageDao.push(message);
        return false;
    }

    private Object handleVoid(Message message, MessageDao messageDao) {
        messageDao.push(message);
        return null;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 查找MessageDao
        MessageDao messageDao = messageDaoMap.get(method);
        if (messageDao == null) {
            return method.invoke(proxy, args);
        }

        // 准备消息
        Message message = new Message();
        message.setArgs(args);

        // 选择消息发送器
        Class<?> returnType = method.getReturnType();
        if (Ack.class.isAssignableFrom(returnType)) {
            return handleAckChannel(message, messageDao);
        } else if (returnType == Boolean.class || returnType == boolean.class) {
            return handleBoolean(message, messageDao);
        } else {
            return handleVoid(message, messageDao);
        }
    }
}
