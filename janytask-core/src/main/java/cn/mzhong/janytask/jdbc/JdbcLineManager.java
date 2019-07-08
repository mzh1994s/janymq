package cn.mzhong.janytask.jdbc;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.jdbc.mapper.MessageMapper;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.queue.LockedLineManager;
import cn.mzhong.janytask.queue.Message;

import java.util.Date;
import java.util.LinkedList;

public class JdbcLineManager extends LockedLineManager {

    protected TaskContext context;
    protected MessageMapper messageMapper;

    public JdbcLineManager(TaskContext context, MessageMapper messageMapper, QueueInfo lineInfo) {
        super(context, lineInfo);
        this.messageMapper = messageMapper;
    }

    public void push(Message message) {
        BytesMessage jdbcMessage = new BytesMessage(message);
        jdbcMessage.setContentBytes(this.dataSerializer.serialize(message.getContent()));
        jdbcMessage.setPushTime(new Date());
        jdbcMessage.setQueueId(ID);
        this.messageMapper.save(jdbcMessage);
    }

    public void done(Message message) {
        BytesMessage jdbcMessage = new BytesMessage(message);
        jdbcMessage.setDoneTime(new Date());
        this.messageMapper.done(jdbcMessage);
    }

    public void error(Message message) {
        BytesMessage jdbcMessage = new BytesMessage(message);
        jdbcMessage.setErrorTime(new Date());
        this.messageMapper.error(jdbcMessage);
    }

    public long length() {
        return this.messageMapper.length(ID);
    }

    @Override
    protected LinkedList<String> idList() {
        return this.messageMapper.keys();
    }

    @Override
    protected Message get(String id) {
        BytesMessage jdbcMessage = this.messageMapper.get(id);
        Object[] content = (Object[]) this.dataSerializer.deserialize(jdbcMessage.getContentBytes());
        jdbcMessage.setContent(content);
        return jdbcMessage;
    }

    @Override
    protected boolean lock(String id) {
        return this.messageMapper.lock(id);
    }

    @Override
    protected boolean unLock(String id) {
        return this.messageMapper.unLock(id);
    }
}
