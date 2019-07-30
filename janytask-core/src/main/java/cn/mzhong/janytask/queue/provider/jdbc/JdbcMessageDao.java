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
        jdbcMessage.setContentBytes(this.serializer.serialize(message.getContent()));
        jdbcMessage.setPushTime(new Date());
        jdbcMessage.setQueueId(id);
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
        return this.messageMapper.length(id);
    }

    @Override
    protected LinkedList<String> queueIdList() {
        return this.messageMapper.keys();
    }

    @Override
    protected Message get(String id) {
        BytesMessage jdbcMessage = this.messageMapper.get(id);
        Object[] content = (Object[]) this.serializer.deserialize(jdbcMessage.getContentBytes());
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
