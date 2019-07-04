package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.LinkedList;

public class MysqlMessageMapper extends AbstractMessageMapper {

    final static Logger Log = LoggerFactory.getLogger(MysqlMessageMapper.class);

    public MysqlMessageMapper(MQContext context, SqlExecutor sqlExecutor, String table) {
        super(context, sqlExecutor, table);
    }

    @Override
    public boolean isTableExists() {
        try {
            this.sqlExecutor.executeQuery("select count(*) from " + table);
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    @Override
    public void createTable() {
        this.sqlExecutor.execute(
                "CREATE TABLE `" + table + "` " +
                        "(`key` CHAR(22) NOT NULL," +
                        "`line_id` VARCHAR(255) NOT NULL," +
                        "`push_time` DATE NOT NULL," +
                        "`done_time` DATE," +
                        "`error_time` DATE," +
                        "`throwable` BLOB," +
                        "`content` BLOB," +
                        "`status` CHAR(1)," +
                        "PRIMARY KEY (`key`)" +
                        ")");
    }

    @Override
    public void save(Message message) {
        this.sqlExecutor.executeUpdate("INSERT INTO `" + table + "`" +
                        "(`key`,`line_id`,`push_time`,`content`,`status`) VALUES (?,?,?,?,?)",
                message.getKey(),
                message.getLineID(),
                message.getPushTime(),
                message.getContent(),
                MESSAGE_STATUS_WAIT);
    }

    @Override
    public LinkedList<String> keys() {
        ResultSetReader reader = new ResultSetReader(
                this.sqlExecutor.executeQuery(
                        "SELECT `KEY` FROM `" + table + "` WHERE `status`=?", MESSAGE_STATUS_WAIT));
        LinkedList<String> keys = new LinkedList<>();
        while (reader.next()) {
            reader.next();
            keys.add(reader.getString(1));
        }
        return keys;
    }

    @Override
    public boolean lock(String key) {
        return 1 == this.sqlExecutor.executeUpdate(
                "UPDATE `" + table + "` SET `status`=? WHERE `key`=? AND `status`=?",
                MESSAGE_STATUS_LOCK, key, MESSAGE_STATUS_WAIT);
    }

    @Override
    public boolean unLock(String key) {
        return 1 == this.sqlExecutor.executeUpdate(
                "UPDATE `" + table + "` SET `status`=? WHERE `key`=? AND `status`=?",
                MESSAGE_STATUS_WAIT, key, MESSAGE_STATUS_LOCK);
    }

    @Override
    public Message get(String key) {
        ResultSet resultSet = this.sqlExecutor.executeQuery("SELECT `content` FROM `" + table + "` WHERE `key`=?", key);
        ResultSetReader reader = new ResultSetReader(resultSet);
        if (reader.next()) {
            Message message = new Message();
            message.setKey(key);
            Object[] content = (Object[]) reader.getObject(1);
            message.setContent(content);
            return message;
        }
        return null;
    }

    @Override
    public void done(Message message) {
        this.sqlExecutor.executeUpdate(
                "UPDATE FROM `" + table + "` SET " +
                        "`status`=?, " +
                        "`done_time`=? " +
                        "WHERE `key`=?",
                MESSAGE_STATUS_DONE,
                message.getDoneTime(),
                message.getKey());
    }

    @Override
    public void error(Message message) {
        this.sqlExecutor.executeUpdate(
                "UPDATE FROM `" + table + "` SET " +
                        "`status`=?, " +
                        "`error_time`=?, " +
                        "`throwable`=? " +
                        "WHERE `key`=?",
                MESSAGE_STATUS_ERROR,
                message.getErrorTime(),
                message.getThrowable(),
                message.getKey());
    }

    @Override
    public long length(String lineID) {
        ResultSet resultSet = this.sqlExecutor.executeQuery(
                "SELECT COUNT(*) FROM `" + table + "` WHERE `line_id`=? AND `status`=?", lineID, MESSAGE_STATUS_WAIT);
        ResultSetReader reader = new ResultSetReader(resultSet);
        if (reader.next()) {
            return reader.getLong(1);
        }
        return 0;
    }
}
