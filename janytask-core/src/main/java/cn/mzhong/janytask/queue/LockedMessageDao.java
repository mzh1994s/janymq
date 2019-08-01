package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.application.TaskContext;

import java.util.LinkedList;

public abstract class LockedMessageDao extends AbstractMessageDao {

    private LinkedList<String> cacheKeys = new LinkedList<String>();

    public LockedMessageDao(TaskContext context, QueueInfo queueInfo) {
        super(context, queueInfo);
    }

    public final Message poll() {
        if (cacheKeys.isEmpty()) {
            cacheKeys = this.keys();
        }
        while (!cacheKeys.isEmpty()) {
            // ShutdownBreak;
            if (context.isShutdown()) {
                break;
            }
            String key = cacheKeys.poll();
            if (this.lock(key)) {
                return this.get(key);
            }
        }
        return null;
    }

    public final void back(Message message) {
        this.unLock(message.getId());
    }

    protected abstract LinkedList<String> keys();

    protected abstract boolean lock(String id);

    protected abstract boolean unLock(String id);
}
