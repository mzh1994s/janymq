package cn.mzhong.janymq.jdbc.mapper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.jdbc.SqlExecutor;

public class MysqlMessageMapper extends AbstractMessageMapper {

    public MysqlMessageMapper(MQContext context, SqlExecutor sqlExecutor, String table) {
        super(context, sqlExecutor, table);
    }

    public boolean isTableExists() {
        return this.sqlExecutor.queryString("SHOW TABLES LIKE ?", new Object[]{table}) != null;
    }

    public void createTable() {
        this.sqlExecutor.execute(
                "CREATE TABLE `" + table + "` " +
                        "(`MESSAGE_ID` CHAR(22) NOT NULL," +
                        "`LINE_ID` VARCHAR(255) NOT NULL," +
                        "`PUSH_TIME` TIMESTAMP NOT NULL," +
                        "`DONE_TIME` TIMESTAMP," +
                        "`ERROR_TIME` TIMESTAMP," +
                        "`THROWABLE` BLOB," +
                        "`CONTENT` BLOB," +
                        "`STATUS` CHAR(1)," +
                        "PRIMARY KEY (`MESSAGE_ID`)," +
                        "INDEX `" + table + "_LINE_ID` (`LINE_ID`)" +
                        ")");
    }

}
