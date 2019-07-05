package cn.mzhong.janymq.line;

import cn.mzhong.janymq.core.MQContext;

import java.util.LinkedList;

public abstract class LockedLineManager extends AbstractLineManager {

    protected LinkedList<String> cacheKeys = new LinkedList<String>();

    public LockedLineManager(MQContext context, LineInfo lineInfo) {
        super(context, lineInfo);
    }

    public Message poll() {
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

    public void back(Message message) {
        this.unLock(message.getKey());
    }

    protected abstract LinkedList<String> keys();

    protected abstract Message get(String key);

    protected abstract boolean lock(String key);

    protected abstract boolean unLock(String key);
}
