package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

/**
 * 流水线管理器生产者，实现{@link QueueProvider}可以支持任何中间件。<br/>
 * 一般情况下，一个新的中间件要对接JSimpleMQ只需要实现{@link QueueProvider}和{@link QueueManager}两个接口<br/>
 */
public interface QueueProvider {

    QueueManager getPiplelineManager(PiplelineInfo pipleline);

    QueueManager getlooplinemanager(LooplineInfo loopLine);

    void init(TaskContext context);
}
