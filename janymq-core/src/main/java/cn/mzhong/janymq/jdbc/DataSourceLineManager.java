package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.DataSerializer;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;

public class DataSourceLineManager implements LineManager {

    final static Logger Log = LoggerFactory.getLogger(DataSourceLineManager.class);

    protected MQContext context;
    protected MessageMapper messageMapper;
    protected String ID;
    protected LinkedList<String> cacheKeys = new LinkedList<>();

    protected DataSerializer dataSerializer;

    public DataSourceLineManager(MQContext context, MessageMapper messageMapper, String ID) {
        this.context = context;
        this.dataSerializer = context.getDataSerializer();
        this.messageMapper = messageMapper;
        this.ID = ID;
    }

    @Override
    public String ID() {
        return ID;
    }

    @Override
    public void push(Message message) {
        JdbcMessage jdbcMessage = new JdbcMessage(message);
        jdbcMessage.setContentBytes(this.dataSerializer.serialize(message.getContent()));
        jdbcMessage.setPushTime(new Date());
        jdbcMessage.setLineID(ID);
        this.messageMapper.save(jdbcMessage);
    }

    @Override
    public Message poll() {
        if (cacheKeys.isEmpty()) {
            cacheKeys.addAll(this.messageMapper.keys());
        }
        while (!cacheKeys.isEmpty()) {
            String key = cacheKeys.poll();
            if (this.messageMapper.lock(key)) {
                JdbcMessage jdbcMessage = this.messageMapper.get(key);
                Object[] content = (Object[]) this.dataSerializer.deserialize(jdbcMessage.getContentBytes());
                jdbcMessage.setContent(content);
                return jdbcMessage;
            }
        }
        return null;
    }

    @Override
    public void back(Message message) {
        this.messageMapper.unLock(message.getKey());
    }

    @Override
    public void done(Message message) {
        JdbcMessage jdbcMessage = new JdbcMessage(message);
        jdbcMessage.setDoneTime(new Date());
        this.messageMapper.done(jdbcMessage);
    }

    @Override
    public void error(Message message) {
        JdbcMessage jdbcMessage = new JdbcMessage(message);
        jdbcMessage.setErrorTime(new Date());
        this.messageMapper.error(jdbcMessage);
    }

    @Override
    public long length() {
        return this.messageMapper.length(ID);
    }
}
