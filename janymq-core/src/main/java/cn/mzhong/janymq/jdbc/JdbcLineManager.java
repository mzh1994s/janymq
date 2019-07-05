package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.*;

import java.util.Date;
import java.util.LinkedList;

public class JdbcLineManager extends LockedLineManager {

    protected MQContext context;
    protected MessageMapper messageMapper;

    public JdbcLineManager(MQContext context, MessageMapper messageMapper, LineInfo lineInfo) {
        super(context, lineInfo);
        this.messageMapper = messageMapper;
    }

    public void push(Message message) {
        BytesMessage jdbcMessage = new BytesMessage(message);
        jdbcMessage.setContentBytes(this.dataSerializer.serialize(message.getContent()));
        jdbcMessage.setPushTime(new Date());
        jdbcMessage.setLineID(ID);
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
    protected LinkedList<String> keys() {
        return this.messageMapper.keys();
    }

    @Override
    protected Message get(String key) {
        BytesMessage jdbcMessage = this.messageMapper.get(key);
        Object[] content = (Object[]) this.dataSerializer.deserialize(jdbcMessage.getContentBytes());
        jdbcMessage.setContent(content);
        return jdbcMessage;
    }

    @Override
    protected boolean lock(String key) {
        return this.messageMapper.lock(key);
    }

    @Override
    protected boolean unLock(String key) {
        return this.messageMapper.unLock(key);
    }
}
