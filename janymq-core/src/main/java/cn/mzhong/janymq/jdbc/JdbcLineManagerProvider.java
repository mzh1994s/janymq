package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.jdbc.mapper.MessageMapper;
import cn.mzhong.janymq.jdbc.mapper.MysqlMessageMapper;
import cn.mzhong.janymq.jdbc.mapper.OracleMessageMapper;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LineManagerProvider;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.line.PiplelineInfo;
import cn.mzhong.janymq.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;

public class JdbcLineManagerProvider implements LineManagerProvider {

    final static Logger Log = LoggerFactory.getLogger(JdbcLineManagerProvider.class);

    protected DataSource dataSource;
    protected String table = "JANYMQ_MESSAGE";
    protected SqlExecutor sqlExecutor;
    protected MessageMapper messageMapper;
    protected MQContext context;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public LineManager getPiplelineManager(PiplelineInfo pipleline) {
        return new JdbcLineManager(context, messageMapper, pipleline);
    }

    public LineManager getlooplinemanager(LooplineInfo loopLine) {
        return new JdbcLineManager(context, messageMapper, loopLine);
    }

    protected String getJdbcDriverClassName() {
        String[] driverClassGetterMethodNames = {
                "getDriverClass", // c3p0、jdbc
                "getDriverClassName", // Druid
        };
        String jdbcDriverClassName = null;
        for (String methodName : driverClassGetterMethodNames) {
            try {
                Method driverClassGetterMethod = dataSource.getClass().getMethod(methodName);
                jdbcDriverClassName = (String) driverClassGetterMethod.invoke(this.dataSource);
                if (!StringUtils.isEmpty(jdbcDriverClassName)) {
                    break;
                }
            } catch (Exception e) {
                // pass
            }
        }
        return jdbcDriverClassName;
    }

    public void init(MQContext context) {
        this.context = context;
        // 表名大写
        this.table = this.table.toUpperCase();
        this.sqlExecutor = new SqlExecutor(this.dataSource);
        Log.debug(this.toString());
        String jdbcDriverClassName = this.getJdbcDriverClassName();
        // Mysql 数据库
        if ("com.mysql.jdbc.Driver".equals(jdbcDriverClassName)
                || "com.mysql.cj.jdbc.Driver".equals(jdbcDriverClassName)) {
            Log.debug("数据库类型：Mysql");
            messageMapper = new MysqlMessageMapper(this.context, this.sqlExecutor, this.table);
        }
        // Oracle 数据库
        else if ("oracle.jdbc.OracleDriver".equals(jdbcDriverClassName)) {
            Log.debug("数据库类型：Oracle");
            messageMapper = new OracleMessageMapper(this.context, this.sqlExecutor, this.table);
        }
        if (messageMapper == null) {
            throw new RuntimeException("不支持的数据库类型！");
        }
        messageMapper.init();
    }

    @Override
    public String toString() {
        return "JdbcLineManagerProvider{" +
                "dataSource=" + dataSource +
                ", table='" + table + '\'' +
                '}';
    }
}
