package cn.mzhong.janytask.queue.provider.any;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.queue.provider.QueueProvider;

/**
 * 由此类实现任意提供商
 */
public class AnyProvider implements QueueProvider {

    QueueProvider internalQueueProvider;

    public void setInternalQueueProvider(QueueProvider internalQueueProvider) {
        this.internalQueueProvider = internalQueueProvider;
    }

    public MessageDao createMessageDao(QueueInfo queueInfo) {
        return internalQueueProvider.createMessageDao(queueInfo);
    }

    public void setContext(TaskContext context) {
        internalQueueProvider.setContext(context);
    }

    public void init() {
        internalQueueProvider.init();
    }
}
