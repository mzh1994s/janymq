package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

import java.util.LinkedList;

public abstract class LockedQueueManager extends AbstractQueueManager {

    protected LinkedList<String> cacheKeys = new LinkedList<String>();

    public LockedQueueManager(TaskContext context, QueueInfo lineInfo) {
        super(context, lineInfo);
    }

    public Message poll() {
        if (cacheKeys.isEmpty()) {
            cacheKeys = this.idList();
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

    public void back(Message message) {
        this.unLock(message.getId());
    }

    protected abstract LinkedList<String> idList();

    protected abstract Message get(String id);

    protected abstract boolean lock(String id);

    protected abstract boolean unLock(String id);
}
