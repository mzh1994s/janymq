package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.serialize.Serializer;

public abstract class AbstractMessageDao implements MessageDao {
    protected String ID;
    protected TaskContext context;
    protected QueueInfo queueInfo;
    protected QueueManager queueManager;

    protected Serializer serializer;

    public String ID() {
        return this.ID;
    }

    public AbstractMessageDao(TaskContext context, QueueInfo queueInfo) {
        this.context = context;
        this.serializer = context.getSerializer();
        this.queueInfo = queueInfo;
        this.ID = queueInfo.ID();
    }
}
