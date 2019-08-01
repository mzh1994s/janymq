package cn.mzhong.janytask.queue.ack;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.application.TaskContextAware;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.worker.TaskExecutor;
import io.netty.util.internal.ConcurrentSet;
import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AckHandler extends TaskExecutor implements TaskContextAware {
    final private Map<Message, HandlerItem> handlerMap = new ConcurrentHashMap<Message, HandlerItem>();

    public AckHandler() {
        super(null, new JanyTask$CronSequenceGenerator("* * * * * ?"));
    }

    public void setContext(TaskContext context) {
        super.context = context;
    }

    public void add(Message message, MessageDao messageDao) {
        handlerMap.put(message, new HandlerItem(message, messageDao));
    }

    public void addListener(Message message, AckListener<Serializable> listener) {
        HandlerItem handlerItem = handlerMap.get(message);
        if (handlerItem != null) {
            handlerItem.listeners.add(listener);
        }
    }

    private Serializable getResult(HandlerItem handlerItem) throws Throwable {
        Message message = handlerItem.messageDao.get(handlerItem.message.getId());
        String status = message.getStatus();
        if (Message.STATUS_DONE.equals(status)) {
            return (Serializable) message.getResult();
        } else if (Message.STATUS_ERROR.equals(status)) {
            throw message.getThrowable();
        }
        return null;
    }

    public Serializable get(Message message) throws InterruptedException, ExecutionException {
        HandlerItem handlerItem = handlerMap.get(message);
        if (handlerItem != null) {
            handlerItem.countDownLatch.await();
            try {
                return getResult(handlerItem);
            } catch (Throwable throwable) {
                throw new ExecutionException(throwable);
            }
        }
        return null;
    }

    public Serializable get(Message message, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        HandlerItem handlerItem = handlerMap.get(message);
        if (handlerItem != null) {
            handlerItem.countDownLatch.await(timeout, unit);
            try {
                return getResult(handlerItem);
            } catch (Throwable throwable) {
                throw new ExecutionException(throwable);
            }
        }
        return null;
    }

    private void setDone(HandlerItem handlerItem, Message message) {
        System.out.println(handlerMap.size());
        handlerMap.remove(handlerItem.message);
        System.out.println(handlerMap.size());
        Object result = message.getResult();
        handlerItem.message.setResult(result);
        handlerItem.countDownLatch.countDown();
        Iterator<AckListener<Serializable>> iterator = handlerItem.listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().done((Serializable) result);
        }
    }

    private void setError(HandlerItem handlerItem, Message message) {
        System.out.println(handlerMap.size());
        handlerMap.remove(handlerItem.message);
        System.out.println(handlerMap.size());
        Throwable throwable = message.getThrowable();
        handlerItem.message.setThrowable(throwable);
        handlerItem.countDownLatch.countDown();
        Iterator<AckListener<Serializable>> iterator = handlerItem.listeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().error(throwable);
        }
    }

    protected void execute() {
        Iterator<HandlerItem> iterator = handlerMap.values().iterator();
        while (iterator.hasNext()) {
            HandlerItem handlerItem = iterator.next();
            String id = handlerItem.message.getId();
            MessageDao messageDao = handlerItem.messageDao;
            Message message = messageDao.get(id);
            String status = message.getStatus();
            // 根据Message的状态来判断是否已经完成
            // 1、已完成
            if (Message.STATUS_DONE.equals(status)) {
                setDone(handlerItem, message);
            }
            // 2、已出错
            else if (Message.STATUS_ERROR.equals(status)) {
                setError(handlerItem, message);
            }
            // 3、其他
        }
    }
}

class HandlerItem {
    final protected Message message;
    final protected MessageDao messageDao;
    final protected CountDownLatch countDownLatch = new CountDownLatch(1);
    final protected Set<AckListener<Serializable>> listeners = new ConcurrentSet<AckListener<Serializable>>();

    public HandlerItem(Message message, MessageDao messageDao) {
        this.message = message;
        this.messageDao = messageDao;
    }
}
