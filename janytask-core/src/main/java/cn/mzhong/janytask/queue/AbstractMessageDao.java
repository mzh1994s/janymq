package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.serialize.Serializer;

public abstract class AbstractMessageDao implements MessageDao {
    protected String id;
    protected TaskContext context;
    protected QueueInfo queueInfo;

    protected Serializer serializer;

    public String getId() {
        return this.id;
    }

    public AbstractMessageDao(TaskContext context, QueueInfo queueInfo) {
        this.context = context;
        this.serializer = context.getSerializer();
        this.queueInfo = queueInfo;
        this.id = queueInfo.getId();
    }
}
