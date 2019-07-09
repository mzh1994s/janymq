package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

public abstract class AbstractQueueManager implements QueueManager {
    protected String ID;
    protected TaskContext context;
    protected QueueInfo lineInfo;

    protected JdkDataSerializer dataSerializer;

    public String ID() {
        return this.ID;
    }

    public AbstractQueueManager(TaskContext context, QueueInfo lineInfo) {
        this.context = context;
        this.dataSerializer = context.getDataSerializer();
        this.lineInfo = lineInfo;
        this.ID = lineInfo.ID();
    }
}
