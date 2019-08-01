package cn.mzhong.janytask.queue.ack;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.application.TaskContextAware;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.worker.TaskExecutor;
import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AckHandler extends TaskExecutor implements TaskContextAware {
    final private Map<Message, HandlerAck> handlerMap = new ConcurrentHashMap<Message, HandlerAck>();

    public AckHandler() {
        super(null, new JanyTask$CronSequenceGenerator("* * * * * ?"));
    }

    public void setContext(TaskContext context) {
        super.context = context;
    }

    public void add(HandlerAck ack) {
        handlerMap.put(ack.getMessage(), ack);
    }

    protected void execute() {
        Iterator<HandlerAck> iterator = handlerMap.values().iterator();
        while (iterator.hasNext()) {
            HandlerAck handlerAck = iterator.next();
            String id = handlerAck.getMessage().getId();
            MessageDao messageDao = handlerAck.getMessageDao();
            Message message = messageDao.get(id);
            String status = message.getStatus();
            // 根据Message的状态来判断是否已经完成
            // 1、已完成
            if (Message.STATUS_DONE.equals(status)) {
                iterator.remove();
                handlerAck.setDone(message);
            }
            // 2、已出错
            else if (Message.STATUS_ERROR.equals(status)) {
                iterator.remove();
                handlerAck.setError(message);
            }
            // 3、其他
        }
    }
}
