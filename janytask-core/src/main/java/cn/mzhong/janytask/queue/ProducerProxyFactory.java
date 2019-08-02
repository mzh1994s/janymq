package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.ack.*;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
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
        // 先发送消息
        messageDao.push(message);
        // 组装Ack对象
        Ack<Serializable> ack = new InternalAck<Serializable>(message, messageDao);
        // 加入AckHandler
        this.ackHandler.add((HandlerAck) ack);
        return ack;
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

/**
 * 使用内部类是为了防止外部使用new关键字使用。本类实现了{@link HandlerAck}接口供
 * {@link AckHandler}调用和控制。用户在任务方上设置返回值类型为{@link Ack}，则会返回
 * 此类的一个实例，用户可以调用此实例的相关方法来完成对任务执行结果的监听。
 *
 * @param <T>
 * @author mzhong
 */
@SuppressWarnings("unchecked")
class InternalAck<T extends Serializable> extends Ack<T> implements HandlerAck {

    private static final long serialVersionUID = 8428868438000651270L;

    private AckStatus status = AckStatus.WAIT;
    private T result;
    private Throwable throwable;
    final private Message message;
    final private MessageDao messageDao;
    final private Set<AckListener<T>> listeners = new HashSet<AckListener<T>>();
    final private CountDownLatch countDownLatch = new CountDownLatch(1);

    public InternalAck(Message message, MessageDao messageDao) {
        super(null);
        this.message = message;
        this.messageDao = messageDao;
    }

    /**
     * 对任务执行结果添加监听器。任务顺利执行完成后，会执行监听器的{@link AckListener#done(Object)}方法。
     * 任务执行过程中发生异常时，也会终止任务，此时则执行监听器的{@link AckListener#error(Throwable)}方法。
     * <p style="color:red">
     * 注意：监听器不是实时执行的，它由任务调度器{@link cn.mzhong.janytask.worker.TaskWorker}统一调度，
     * 并且对任务的检测周期是1秒钟，如果不是特殊情况请慎用{@link #listen(AckListener)}、{@link #get()}、
     * {@link #get(long, TimeUnit)}这三个方法，他们可能会让你的线程浪费更多的无用时间去等待结果返回。
     * <p/>
     *
     * @param listener
     * @return
     */
    public synchronized Ack<T> listen(AckListener<T> listener) {
        // 如果执行结果都已经返回了，直接执行监听函数
        switch (status) {
            case DONE:
                listener.done(result);
                break;
            case ERROR:
                listener.error(throwable);
                break;
            default:
                listeners.add(listener);
        }
        return this;
    }

    /**
     * 获取任务执行的结果，调用此方法会阻塞，直到任务执行完成后才会返回结果。
     * <p style="color:red">
     * 注意：此方法并非同步调用，结果不是实时返回的，它由任务调度器{@link cn.mzhong.janytask.worker.TaskWorker}
     * 统一调度，并且对任务的检测周期是1秒钟，如果不是特殊情况请慎用{@link #listen(AckListener)}、{@link #get()}、
     * {@link #get(long, TimeUnit)}这三个方法，他们可能会让你的线程浪费更多的无用时间去等待结果返回。
     * <p/>
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public T get() throws InterruptedException, ExecutionException {
        this.countDownLatch.await();
        if (status == AckStatus.ERROR) {
            throw new ExecutionException(this.throwable);
        }
        return this.result;
    }

    /**
     * 获取任务执行的结果，调用此方法会阻塞，直到等待时间超过设定的时间才会返回结果。
     * <p style="color:red">
     * 注意：结果不是实时返回的，它由任务调度器{@link cn.mzhong.janytask.worker.TaskWorker}统一调度，
     * 并且对任务的检测周期是1秒钟，如果不是特殊情况请慎用{@link #listen(AckListener)}、{@link #get()}、
     * {@link #get(long, TimeUnit)}这三个方法，他们可能会让你的线程浪费更多的无用时间去等待结果返回。
     * <p/>
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        this.countDownLatch.await(timeout, unit);
        if (status == AckStatus.ERROR) {
            throw new ExecutionException(this.throwable);
        }
        return this.result;
    }

    /**
     * 提供给{@link AckHandler}使用，完成任务消耗，保存任务结果，释放在{@link #get()}、
     * {@link #get(long, TimeUnit)}上的阻塞效果，并且执行所有监听器。
     *
     * @param message
     */
    public synchronized void setDone(Message message) {
        this.status = AckStatus.DONE;
        this.countDownLatch.countDown();
        this.result = (T) message.getResult();
        this.message.setResult(result);
        Iterator<AckListener<T>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().done(result);
        }
    }

    /**
     * 提供给{@link AckHandler}使用，任务中途发生异常，保存任务异常，释放在{@link #get()}、
     * {@link #get(long, TimeUnit)}上的阻塞效果，并且执行所有监听器。
     *
     * @param message
     */
    public synchronized void setError(Message message) {
        this.status = AckStatus.ERROR;
        this.countDownLatch.countDown();
        this.throwable = message.getThrowable();
        this.message.setThrowable(throwable);
        Iterator<AckListener<T>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().error(throwable);
        }
    }

    public Message getMessage() {
        return message;
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

}

/**
 * ACK状态枚举
 */
enum AckStatus {
    /**
     * 等待状态
     */
    WAIT,
    /**
     * 已完成
     */
    DONE,
    /**
     * 已发生错误
     */
    ERROR
}
