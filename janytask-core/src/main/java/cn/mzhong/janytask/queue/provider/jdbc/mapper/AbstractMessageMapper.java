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

    public void push(BytesMessage message) {
        this.sqlExecutor.update("INSERT INTO " + table +
                        "(MESSAGE_ID,QUEUE_ID,CREATE_TIME,PUSH_TIME,ARGS,RESULT,STATUS) VALUES (?,?,?,?,?,?,?)",
                message.getId(),
                message.getQueueId(),
                new Timestamp(message.getCreateTime().getTime()),
                new Timestamp(message.getPushTime().getTime()),
                message.getArgsBytes(),
                message.getResultBytes(),
                Message.Status.Wait.value);
    }

    public LinkedList<String> keys(String queueId) {
        return this.sqlExecutor.queryList(
                "SELECT MESSAGE_ID FROM " + table + " WHERE QUEUE_ID=? AND STATUS=?",
                new Object[]{queueId, Message.Status.Wait.value},
                new PRInvoker<ResultSet, String>() {
                    public String invoke(ResultSet resultSet) throws Exception {
                        return resultSet.getString(1);
                    }
                });
    }

    public boolean lock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE " + table + " SET STATUS=? WHERE MESSAGE_ID=? AND STATUS=?",
                Message.Status.Lock.value, key, Message.Status.Wait.value);
    }

    public boolean unLock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE " + table + " SET STATUS=? WHERE MESSAGE_ID=? AND STATUS=?",
                Message.Status.Wait.value, key, Message.Status.Lock.value);
    }

    public BytesMessage get(final String key) {
        return this.sqlExecutor.query(
                "SELECT ARGS,RESULT,THROWABLE,STATUS FROM " + table + " WHERE MESSAGE_ID=?",
                new Object[]{key},
                new PRInvoker<ResultSet, BytesMessage>() {
                    public BytesMessage invoke(ResultSet resultSet) throws Exception {
                        BytesMessage message = new BytesMessage();
                        message.setId(key);
                        message.setArgsBytes(resultSet.getBytes(1));
                        message.setResultBytes(resultSet.getBytes(2));
                        message.setThrowableBytes(resultSet.getBytes(3));
                        message.setStatus(Message.Status.get(resultSet.getString(4)));
                        return message;
                    }
                });
    }

    public void done(BytesMessage message) {
        this.sqlExecutor.update(
                "UPDATE " + table + " SET " +
                        "STATUS=?, " +
                        "DONE_TIME=?, " +
                        "RESULT=? " +
                        "WHERE MESSAGE_ID=?",
                Message.Status.Done.value,
                new Timestamp(message.getDoneTime().getTime()),
                message.getResultBytes(),
                message.getId());
    }

    public void error(BytesMessage message) {
        this.sqlExecutor.update(
                "UPDATE " + table + " SET " +
                        "STATUS=?, " +
                        "ERROR_TIME=?, " +
                        "THROWABLE=? " +
                        "WHERE MESSAGE_ID=?",
                Message.Status.Error.value,
                new Timestamp(message.getErrorTime().getTime()),
                message.getThrowableBytes(),
                message.getId());
    }

    public long length(String lineID) {
        return this.sqlExecutor.queryLong(
                "SELECT COUNT(*) FROM " + table + " WHERE QUEUE_ID=? AND STATUS=?",
                new Object[]{lineID, Message.Status.Wait.value});
    }
}
