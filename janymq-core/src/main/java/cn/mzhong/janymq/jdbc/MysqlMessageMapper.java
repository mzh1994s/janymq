package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.core.MQContext;

import java.sql.ResultSet;
import java.util.List;

public class MysqlMessageMapper extends AbstractMessageMapper {

    public MysqlMessageMapper(MQContext context, SqlExecutor sqlExecutor, String table) {
        super(context, sqlExecutor, table);
    }

    @Override
    public boolean isTableExists() {
        return this.sqlExecutor.query(
                "show tables like ?",
                new Object[]{table},
                new ResultSetIterator<String>() {
                    @Override
                    public String read(ResultSet resultSet) throws Exception {
                        return resultSet.getString(1);
                    }
                }) != null;
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
                        "PRIMARY KEY (`key`)," +
                        "INDEX `" + table + "_line_id` (`line_id`)" +
                        ")");
    }

    @Override
    public void save(JdbcMessage message) {
        this.sqlExecutor.update("INSERT INTO `" + table + "`" +
                        "(`key`,`line_id`,`push_time`,`content`,`status`) VALUES (?,?,?,?,?)",
                message.getKey(),
                message.getLineID(),
                message.getPushTime(),
                message.getContentBytes(),
                MESSAGE_STATUS_WAIT);
    }

    @Override
    public List<String> keys() {
        return this.sqlExecutor.queryList(
                "SELECT `KEY` FROM `" + table + "` WHERE `status`=?",
                new Object[]{MESSAGE_STATUS_WAIT},
                new ResultSetIterator<String>() {
                    @Override
                    public String read(ResultSet resultSet) throws Exception {
                        return resultSet.getString(1);
                    }
                });
    }

    @Override
    public boolean lock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE `" + table + "` SET `status`=? WHERE `key`=? AND `status`=?",
                MESSAGE_STATUS_LOCK, key, MESSAGE_STATUS_WAIT);
    }

    @Override
    public boolean unLock(String key) {
        return 1 == this.sqlExecutor.update(
                "UPDATE `" + table + "` SET `status`=? WHERE `key`=? AND `status`=?",
                MESSAGE_STATUS_WAIT, key, MESSAGE_STATUS_LOCK);
    }

    @Override
    public JdbcMessage get(String key) {
        return this.sqlExecutor.query(
                "SELECT `content` FROM `" + table + "` WHERE `key`=?",
                new Object[]{key},
                new ResultSetIterator<JdbcMessage>() {
                    @Override
                    public JdbcMessage read(ResultSet resultSet) throws Exception {
                        if (resultSet.next()) {
                            JdbcMessage message = new JdbcMessage();
                            message.setKey(key);
                            message.setContentBytes(resultSet.getBytes(1));
                            return message;
                        }
                        return null;
                    }
                });
    }

    @Override
    public void done(JdbcMessage message) {
        this.sqlExecutor.update(
                "UPDATE FROM `" + table + "` SET " +
                        "`status`=?, " +
                        "`done_time`=? " +
                        "WHERE `key`=?",
                MESSAGE_STATUS_DONE,
                message.getDoneTime(),
                message.getKey());
    }

    @Override
    public void error(JdbcMessage message) {
        this.sqlExecutor.update(
                "UPDATE FROM `" + table + "` SET " +
                        "`status`=?, " +
                        "`error_time`=?, " +
                        "`throwable`=? " +
                        "WHERE `key`=?",
                MESSAGE_STATUS_ERROR,
                message.getErrorTime(),
                message.getThrowableBytes(),
                message.getKey());
    }

    @Override
    public long length(String lineID) {
        return this.sqlExecutor.query(
                "SELECT COUNT(*) FROM `" + table + "` WHERE `line_id`=? AND `status`=?",
                new Object[]{lineID, MESSAGE_STATUS_WAIT},
                new ResultSetIterator<Long>() {
                    @Override
                    public Long read(ResultSet resultSet) throws Exception {
                        if (resultSet.next()) {
                            return resultSet.getLong(1);
                        }
                        return 0L;
                    }
                });
    }
}
