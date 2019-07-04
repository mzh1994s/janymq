package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;

public class DataSourceLineManager implements LineManager {

    final static Logger Log = LoggerFactory.getLogger(DataSourceLineManager.class);

    protected MessageMapper messageMapper;
    protected String ID;
    protected LinkedList<String> cacheKeys = new LinkedList<>();

    public DataSourceLineManager(MessageMapper messageMapper, String ID) {
        this.messageMapper = messageMapper;
        this.ID = ID;
    }

    @Override
    public String ID() {
        return ID;
    }

    @Override
    public void push(Message message) {
        message.setPushTime(new Date());
        message.setLineID(ID);
        this.messageMapper.save(message);
    }

    @Override
    public Message poll() {
        if (cacheKeys.isEmpty()) {
            cacheKeys = this.messageMapper.keys();
        }
        while (!cacheKeys.isEmpty()) {
            String key = cacheKeys.poll();
            if (this.messageMapper.lock(key)) {
                return this.messageMapper.get(key);
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
        this.messageMapper.done(message);
    }

    @Override
    public void error(Message message) {
        this.messageMapper.error(message);
    }

    @Override
    public long length() {
        return this.messageMapper.length(ID);
    }
}
