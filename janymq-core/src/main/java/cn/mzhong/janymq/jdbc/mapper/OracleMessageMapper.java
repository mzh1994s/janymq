package cn.mzhong.janymq.jdbc.mapper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.jdbc.SqlExecutor;
import cn.mzhong.janymq.tool.PRInvoker;

import java.sql.ResultSet;

public class OracleMessageMapper extends AbstractMessageMapper {

    public OracleMessageMapper(MQContext context, SqlExecutor sqlExecutor, String table) {
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
                "LINE_ID VARCHAR2(255) NOT NULL," +
                "PUSH_TIME TIMESTAMP NOT NULL," +
                "DONE_TIME TIMESTAMP," +
                "ERROR_TIME TIMESTAMP," +
                "THROWABLE BLOB," +
                "CONTENT BLOB," +
                "STATUS CHAR(1)," +
                "CONSTRAINT " + table + "_PK PRIMARY KEY(MESSAGE_ID)" +
                ")");
        this.sqlExecutor.execute("CREATE INDEX " + table + "_LINE_ID_INDEX ON " + table + "(LINE_ID)");
    }
}
