package cn.mzhong.janytask.jdbc.mapper;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.jdbc.DataSourceHelper;
import cn.mzhong.janytask.tool.PRInvoker;

import java.sql.ResultSet;

public class OracleMessageMapper extends AbstractMessageMapper {

    public OracleMessageMapper(TaskContext context, DataSourceHelper sqlExecutor, String table) {
        super(context, sqlExecutor, table);
    }

    public boolean isTableExists() {
        return this.sqlExecutor.query("SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = ?",
                new Object[]{table},
                new PRInvoker<ResultSet, Integer>() {
                    public Integer invoke(ResultSet resultSet) throws Exception {
                        return resultSet.getInt(1);
                    }
                }) == 1;
    }

    public void createTable() {
        this.sqlExecutor.execute("CREATE TABLE " + table +
                "(MESSAGE_ID CHAR(22) NOT NULL," +
                "QUEUE_ID VARCHAR2(255) NOT NULL," +
                "PUSH_TIME TIMESTAMP NOT NULL," +
                "DONE_TIME TIMESTAMP," +
                "ERROR_TIME TIMESTAMP," +
                "THROWABLE BLOB," +
                "CONTENT BLOB," +
                "STATUS CHAR(1)," +
                "CONSTRAINT " + table + "_PK PRIMARY KEY(MESSAGE_ID)" +
                ")");
        this.sqlExecutor.execute("CREATE INDEX " + table + "_QUEUE_ID_INDEX ON " + table + "(QUEUE_ID)");
    }
}
