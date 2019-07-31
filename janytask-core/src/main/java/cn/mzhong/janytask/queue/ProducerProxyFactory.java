package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.ack.FutureHandler;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private FutureHandler futureHandler;

    ProducerInvocationHandler(TaskContext context) {
        QueueManager queueManager = context.getApplication().getQueueManager();
        this.messageDaoMap = queueManager.getMessageDaoMap();
        this.futureHandler = queueManager.getFutureHandler();
    }

    private Object handleAckChannel(final Message message, MessageDao messageDao) {
        // 先发送
        messageDao.push(message);
        // 加入AckHandler
        this.futureHandler.add(message, messageDao);
        return new Future<Serializable>() {

            public boolean cancel(boolean mayInterruptIfRunning) {
                return futureHandler.cancel(message, mayInterruptIfRunning);
            }

            public boolean isCancelled() {
                return futureHandler.isCancelled(message);
            }

            public boolean isDone() {
                return futureHandler.isDone(message);
            }

            public Serializable get() throws InterruptedException, ExecutionException {
                return futureHandler.get(message);
            }

            public Serializable get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return futureHandler.get(message, timeout, unit);
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
        MessageDao messageDao = messageDaoMap.get(method);
        if (messageDao == null) {
            return method.invoke(proxy, args);
        }
        Message message = new Message();
        message.setContent(args);
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(Future.class)) {
            return handleAckChannel(message, messageDao);
        } else if (returnType.isAssignableFrom(Boolean.class)) {
            return handleBoolean(message, messageDao);
        } else {
            return handleVoid(message, messageDao);
        }
    }
}
