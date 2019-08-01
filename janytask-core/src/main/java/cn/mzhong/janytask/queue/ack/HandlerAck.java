package cn.mzhong.janytask.queue.ack;

import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.MessageDao;

public interface HandlerAck {

    void setDone(Message message);

    void setError(Message message);

    Message getMessage();

    MessageDao getMessageDao();
}
