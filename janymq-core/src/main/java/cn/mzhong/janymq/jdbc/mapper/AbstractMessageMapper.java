package cn.mzhong.janymq.jdbc.mapper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.jdbc.BytesMessage;
import cn.mzhong.janymq.jdbc.SqlExecutor;
import cn.mzhong.janymq.tool.PRInvoker;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedList;

public abstract class AbstractMessageMapper implements MessageMapper {
    protected MQContext context;
    protected SqlExecutor sqlExecutor;
    protected String table;

    public AbstractMessageMapper(MQContext context, SqlExecutor sqlExecutor, String table) {
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
                        "(MESSAGE_ID,LINE_ID,PUSH_TIME,CONTENT,STATUS) VALUES (?,?,?,?,?)",
                message.getId(),
                message.getLineId(),
                new Timestamp(message.getPushTime().getTime()),
                message.getContentBytes(),
                MESSAGE_STATUS_WAIT);
    }

    public LinkedList<String> keys() {
        return this.sqlExecutor.queryList(
                "SELECT MESSAGE_ID FROM " + table + " WHERE STATUS=?",
                new Object[]{MESSAGE_STATUS_WAIT},
                new PRInvoker<ResultSet, String>() {
                    public String invoke(ResultSet resultSet) throws Exception {
                        return resultSet.getString(1);
                    }
                });
    }

    public boolean lock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE " + table + " SET STATUS=? WHERE MESSAGE_ID=? AND STATUS=?",
                MESSAGE_STATUS_LOCK, key, MESSAGE_STATUS_WAIT);
    }

    public boolean unLock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE " + table + " SET STATUS=? WHERE MESSAGE_ID=? AND STATUS=?",
                MESSAGE_STATUS_WAIT, key, MESSAGE_STATUS_LOCK);
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
                "UPDATE FROM " + table + " SET " +
                        "STATUS=?, " +
                        "DONE_TIME=? " +
                        "WHERE MESSAGE_ID=?",
                MESSAGE_STATUS_DONE,
                new Timestamp(message.getDoneTime().getTime()),
                message.getId());
    }

    public void error(BytesMessage message) {
        this.sqlExecutor.update(
                "UPDATE FROM " + table + " SET " +
                        "STATUS=?, " +
                        "ERROR_TIME=?, " +
                        "THROWABLE=? " +
                        "WHERE MESSAGE_ID=?",
                MESSAGE_STATUS_ERROR,
                new Timestamp(message.getErrorTime().getTime()),
                message.getThrowableBytes(),
                message.getId());
    }

    public long length(String lineID) {
        return this.sqlExecutor.queryLong(
                "SELECT COUNT(*) FROM " + table + " WHERE LINE_ID=? AND STATUS=?",
                new Object[]{lineID, MESSAGE_STATUS_WAIT});
    }
}
