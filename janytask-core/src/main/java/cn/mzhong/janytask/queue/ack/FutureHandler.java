package cn.mzhong.janytask.queue.ack;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.application.TaskContextAware;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.worker.TaskExecutor;
import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FutureHandler extends TaskExecutor implements TaskContextAware {
    final private Map<Message, HandlerItem> handlerMap = new ConcurrentHashMap<Message, HandlerItem>();

    public FutureHandler() {
        super(null, new JanyTask$CronSequenceGenerator("* * * * * ?"));
    }

    public void setContext(TaskContext context) {
        super.context = context;
    }

    public void add(Message message, MessageDao messageDao) {
        handlerMap.put(message, new HandlerItem(message, messageDao));
    }

    public boolean cancel(Message message, boolean mayInterruptIfRunning) {
        HandlerItem handlerItem = handlerMap.remove(message);
        if(handlerItem != null){
            handlerItem.countDownLatch.countDown();
            return true;
        }
        return false;
    }

    public boolean isCancelled(Message message) {
        return !handlerMap.containsKey(message);
    }

    public boolean isDone(Message message) {
        return !handlerMap.containsKey(message);
    }

    public Serializable get(Message message) throws InterruptedException {
        HandlerItem handlerItem = handlerMap.get(message);
        if(handlerItem != null){
            handlerItem.countDownLatch.await();
        }
        return null;
    }

    public Serializable get(Message message, long timeout, TimeUnit unit) throws InterruptedException {
        HandlerItem handlerItem = handlerMap.get(message);
        if(handlerItem != null){
            handlerItem.countDownLatch.await(timeout, unit);
            // 需要一个可以得到返回值方法的方法
//            return handlerItem.messageDao.
        }
        return null;
    }

    protected void execute() {
        Iterator<HandlerItem> iterator = handlerMap.values().iterator();
        while (iterator.hasNext()){
            HandlerItem handlerItem = iterator.next();
            String id = handlerItem.message.getId();
            // messageDao新增一个isDone方法，如果done了，则执行
            // handlerItem.countDownLatch.countDown();
        }
    }
}

class HandlerItem {
    protected Message message;
    protected MessageDao messageDao;
    protected CountDownLatch countDownLatch = new CountDownLatch(1);

    public HandlerItem(Message message, MessageDao messageDao) {
        this.message = message;
        this.messageDao = messageDao;
    }
}
