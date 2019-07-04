package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.core.MQContext;

public abstract class AbstractMessageMapper implements MessageMapper {
    protected MQContext context;
    protected SqlExecutor sqlExecutor;
    protected String table;

    public AbstractMessageMapper(MQContext context, SqlExecutor sqlExecutor, String table) {
        this.context = context;
        this.sqlExecutor = sqlExecutor;
        this.table = table;
    }

    @Override
    public void init() {
        if (!isTableExists()) {
            createTable();
        }
    }
}
