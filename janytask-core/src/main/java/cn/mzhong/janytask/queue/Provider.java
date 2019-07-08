package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.TaskContext;

/**
 * 流水线管理器生产者，实现{@link Provider}可以支持任何中间件。<br/>
 * 一般情况下，一个新的中间件要对接JSimpleMQ只需要实现{@link Provider}和{@link LineManager}两个接口<br/>
 */
public interface Provider {

    LineManager getPiplelineManager(PiplelineInfo pipleline);

    LineManager getlooplinemanager(LooplineInfo loopLine);

    void init(TaskContext context);
}
