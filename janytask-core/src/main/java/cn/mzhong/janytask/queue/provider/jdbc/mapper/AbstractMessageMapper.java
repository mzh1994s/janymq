package cn.mzhong.janytask.queue.provider.jdbc.mapper;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.provider.jdbc.BytesMessage;
import cn.mzhong.janytask.queue.provider.jdbc.DataSourceHelper;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.tool.PRInvoker;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedList;

public abstract class AbstractMessageMapper implements MessageMapper {
    protected TaskContext context;
    protected DataSourceHelper sqlExecutor;
    protected String table;

    public AbstractMessageMapper(TaskContext context, DataSourceHelper sqlExecutor, String table) {
        this.context = context;
        this.sqlExecutor = sqlExecutor;
        this.table = table;
    }

    public void init() {
        if (!isTableExists()) {
            createTable();
        }
    }

    public void save(BytesMessage message) {
        this.sqlExecutor.update("INSERT INTO " + table +
                        "(MESSAGE_ID,QUEUE_ID,PUSH_TIME,CONTENT,STATUS) VALUES (?,?,?,?,?)",
                message.getId(),
                message.getQueueId(),
                new Timestamp(message.getPushTime().getTime()),
                message.getContentBytes(),
                Message.STATUS_WAIT);
    }

    public LinkedList<String> keys() {
        return this.sqlExecutor.queryList(
                "SELECT MESSAGE_ID FROM " + table + " WHERE STATUS=?",
                new Object[]{Message.STATUS_WAIT},
                new PRInvoker<ResultSet, String>() {
                    public String invoke(ResultSet resultSet) throws Exception {
                        return resultSet.getString(1);
                    }
                });
    }

    public boolean lock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE " + table + " SET STATUS=? WHERE MESSAGE_ID=? AND STATUS=?",
                Message.STATUS_LOCK, key, Message.STATUS_WAIT);
    }

    public boolean unLock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE " + table + " SET STATUS=? WHERE MESSAGE_ID=? AND STATUS=?",
                Message.STATUS_WAIT, key, Message.STATUS_LOCK);
    }

    public BytesMessage get(final String key) {
        return this.sqlExecutor.query(
                "SELECT CONTENT FROM " + table + " WHERE MESSAGE_ID=?",
                new Object[]{key},
                new PRInvoker<ResultSet, BytesMessage>() {
                    public BytesMessage invoke(ResultSet resultSet) throws Exception {
                        BytesMessage message = new BytesMessage();
                        message.setId(key);
                        message.setContentBytes(resultSet.getBytes(1));
                        return message;
                    }
                });
    }

    public void done(BytesMessage message) {
        this.sqlExecutor.update(
                "UPDATE " + table + " SET " +
                        "STATUS=?, " +
                        "DONE_TIME=? " +
                        "WHERE MESSAGE_ID=?",
                Message.STATUS_DONE,
                new Timestamp(message.getDoneTime().getTime()),
                message.getId());
    }

    public void error(BytesMessage message) {
        this.sqlExecutor.update(
                "UPDATE " + table + " SET " +
                        "STATUS=?, " +
                        "ERROR_TIME=?, " +
                        "THROWABLE=? " +
                        "WHERE MESSAGE_ID=?",
                Message.STATUS_ERROR,
                new Timestamp(message.getErrorTime().getTime()),
                message.getThrowableBytes(),
                message.getId());
    }

    public long length(String lineID) {
        return this.sqlExecutor.queryLong(
                "SELECT COUNT(*) FROM " + table + " WHERE QUEUE_ID=? AND STATUS=?",
                new Object[]{lineID, Message.STATUS_WAIT});
    }
}
