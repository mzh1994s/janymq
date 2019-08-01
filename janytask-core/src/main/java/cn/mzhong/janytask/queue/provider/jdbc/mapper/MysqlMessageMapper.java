package cn.mzhong.janytask.queue.provider.jdbc.mapper;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.provider.jdbc.DataSourceHelper;

public class MysqlMessageMapper extends AbstractMessageMapper {

    public MysqlMessageMapper(TaskContext context, DataSourceHelper sqlExecutor, String table) {
        super(context, sqlExecutor, table);
    }

    public boolean isTableExists() {
        return this.sqlExecutor.queryString("SHOW TABLES LIKE ?", new Object[]{table}) != null;
    }

    public void createTable() {
        this.sqlExecutor.execute(
                "CREATE TABLE `" + table + "` " +
                        "(`MESSAGE_ID` CHAR(22) NOT NULL," +
                        "`QUEUE_ID` VARCHAR(255) NOT NULL," +
                        "`CREATE_TIME` TIMESTAMP NOT NULL," +
                        "`PUSH_TIME` TIMESTAMP NOT NULL," +
                        "`DONE_TIME` TIMESTAMP," +
                        "`ERROR_TIME` TIMESTAMP," +
                        "`THROWABLE` BLOB," +
                        "`ARGS` BLOB," +
                        "`RESULT` BLOB," +
                        "`STATUS` CHAR(1)," +
                        "PRIMARY KEY (`MESSAGE_ID`)," +
                        "INDEX `" + table + "_QUEUE_ID` (`QUEUE_ID`)" +
                        ")");
    }

}
