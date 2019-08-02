package cn.mzhong.janytask.queue.ack;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.application.TaskContextAware;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.worker.TaskExecutor;
import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AckHandler extends TaskExecutor implements TaskContextAware {

    final private List<HandlerAck> handlerAcks = new LinkedList<HandlerAck>();

    public AckHandler() {
        super(null, new JanyTask$CronSequenceGenerator("* * * * * ?"));
    }

    public void setContext(TaskContext context) {
        super.context = context;
    }

    public void add(HandlerAck ack) {
        handlerAcks.add(ack);
    }

    protected void execute() {
        Iterator<HandlerAck> iterator = handlerAcks.iterator();
        while (iterator.hasNext()) {
            HandlerAck handlerAck = iterator.next();
            String id = handlerAck.getMessage().getId();
            MessageDao messageDao = handlerAck.getMessageDao();
            Message message = messageDao.get(id);
            Message.Status status = message.getStatus();
            // 根据Message的状态来判断是否已经完成
            // 1、已完成
            if (status == Message.Status.Done) {
                iterator.remove();
                handlerAck.setDone(message);
            }
            // 2、已出错
            else if (status == Message.Status.Error) {
                iterator.remove();
                handlerAck.setError(message);
            }
            // 3、其他
        }
    }
}
