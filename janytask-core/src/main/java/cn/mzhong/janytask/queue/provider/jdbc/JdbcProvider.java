package cn.mzhong.janytask.queue.provider.jdbc;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.queue.provider.AbstractProvider;
import cn.mzhong.janytask.queue.provider.jdbc.mapper.MessageMapper;
import cn.mzhong.janytask.queue.provider.jdbc.mapper.MysqlMessageMapper;
import cn.mzhong.janytask.queue.provider.jdbc.mapper.OracleMessageMapper;
import cn.mzhong.janytask.tool.PRInvoker;
import cn.mzhong.janytask.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcProvider extends AbstractProvider {

    final static Logger Log = LoggerFactory.getLogger(JdbcProvider.class);

    protected DataSource dataSource;
    protected String table = "JANYTASK_MESSAGE";
    protected DataSourceHelper dataSourceHelper;
    protected MessageMapper messageMapper;
    protected TaskContext context;

    public void setContext(TaskContext context) {
        this.context = context;
    }

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

    public MessageDao createMessageDao(QueueInfo queueInfo) {
        return new JdbcMessageDao(context, messageMapper, queueInfo);
    }

    protected String getDriverName() {
        return this.dataSourceHelper.openConnection(new PRInvoker<Connection, String>() {
            public String invoke(Connection connection) throws Exception {
                return connection.getMetaData().getDriverName();
            }
        });
    }

    protected DriverType getDriverType(String driverName) {
        Assert.notNull(driverName, "参数driverName不能为空！");
        String trimedAndUpperedDriverName = driverName.toUpperCase().trim();
        for (DriverType type : DriverType.values()) {
            if (trimedAndUpperedDriverName.contains(type.name())) {
                return type;
            }
        }
        return DriverType.UNKNOWN;
    }

    public void init() {
        // 表名大写
        this.table = this.table.toUpperCase();
        this.dataSourceHelper = new DataSourceHelper(this.dataSource);
        if (Log.isDebugEnabled()) {
            Log.debug(this.toString());
        }
        String driverName = this.getDriverName();
        DriverType driverType = this.getDriverType(driverName);
        switch (driverType) {
            case MYSQL:
                Log.debug("数据库类型：Mysql");
                messageMapper = new MysqlMessageMapper(this.context, this.dataSourceHelper, this.table);
                break;
            case ORACLE:
                Log.debug("数据库类型：Oracle");
                messageMapper = new OracleMessageMapper(this.context, this.dataSourceHelper, this.table);
                break;
            default:
                throw new RuntimeException("暂不支持的数据库类型：" + driverName);
        }
        messageMapper.init();
    }

    @Override
    public String toString() {
        return "JdbcProvider{" +
                "dataSource=" + dataSource +
                ", table='" + table + '\'' +
                '}';
    }
}

/**
 * 驱动类型枚举
 */
enum DriverType {

    /**
     * MYSQL驱动
     */
    MYSQL,
    /**
     * ORACLE驱动
     */
    ORACLE,
    /**
     * SQLSERVER驱动
     */
    SQLSERVER,
    /**
     * DB2驱动
     */
    DB2,
    /**
     * 未知的，暂时不支持的
     */
    UNKNOWN;
}
