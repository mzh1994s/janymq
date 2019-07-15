package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

public abstract class AbstractMessageDao implements MessageDao {
    protected String ID;
    protected TaskContext context;
    protected QueueInfo queueInfo;

    protected JdkDataSerializer dataSerializer;

    public String ID() {
        return this.ID;
    }

    public AbstractMessageDao(TaskContext context, QueueInfo queueInfo) {
        this.context = context;
        this.dataSerializer = context.getDataSerializer();
        this.queueInfo = queueInfo;
        this.ID = queueInfo.ID();
    }
}
