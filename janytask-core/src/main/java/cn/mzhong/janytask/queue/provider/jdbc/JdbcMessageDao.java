package cn.mzhong.janytask.queue.provider.jdbc;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.provider.jdbc.mapper.MessageMapper;
import cn.mzhong.janytask.queue.LockedMessageDao;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.QueueInfo;

import java.util.Date;
import java.util.LinkedList;

public class JdbcMessageDao extends LockedMessageDao {

    protected TaskContext context;
    protected MessageMapper messageMapper;

    public JdbcMessageDao(TaskContext context, MessageMapper messageMapper, QueueInfo lineInfo) {
        super(context, lineInfo);
        this.messageMapper = messageMapper;
    }

    public void push(Message message) {
        BytesMessage jdbcMessage = new BytesMessage(message);
        jdbcMessage.setArgsBytes(serializer.serialize(message.getArgs()));
        jdbcMessage.setPushTime(new Date());
        jdbcMessage.setQueueId(id);
        messageMapper.push(jdbcMessage);
    }

    public void done(Message message) {
        BytesMessage jdbcMessage = new BytesMessage(message);
        jdbcMessage.setDoneTime(new Date());
        if (jdbcMessage.getResult() != null) {
            jdbcMessage.setResultBytes(serializer.serialize(jdbcMessage.getResult()));
        }
        messageMapper.done(jdbcMessage);
    }

    public void error(Message message) {
        BytesMessage jdbcMessage = new BytesMessage(message);
        jdbcMessage.setErrorTime(new Date());
        if (message.getThrowable() != null) {
            jdbcMessage.setThrowableBytes(serializer.serialize(message.getThrowable()));
        }
        messageMapper.error(jdbcMessage);
    }

    public long length() {
        return messageMapper.length(id);
    }

    @Override
    protected LinkedList<String> keys() {
        return messageMapper.keys(id);
    }

    public Message get(String id) {
        BytesMessage bytesMessage = messageMapper.get(id);
        if (bytesMessage.getArgsBytes() != null) {
            bytesMessage.setArgs((Object[]) serializer.deserialize(bytesMessage.getArgsBytes()));
        }
        if (bytesMessage.getResultBytes() != null) {
            bytesMessage.setResult(serializer.deserialize(bytesMessage.getResultBytes()));
        }
        if (bytesMessage.getThrowableBytes() != null) {
            bytesMessage.setThrowable((Throwable) serializer.deserialize(bytesMessage.getThrowableBytes()));
        }
        return bytesMessage;
    }

    @Override
    protected boolean lock(String id) {
        return messageMapper.lock(id);
    }

    @Override
    protected boolean unLock(String id) {
        return messageMapper.unLock(id);
    }
}
