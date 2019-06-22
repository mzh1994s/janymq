package cn.mzhong.janymq.line;

import cn.mzhong.janymq.core.MQContext;

/**
 * 流水线管理器生产者，实现{@link LineManagerProvider}可以支持任何中间件。<br/>
 * 一般情况下，一个新的中间件要对接JSimpleMQ只需要实现{@link LineManagerProvider}和{@link LineManager}两个接口<br/>
 */
public interface LineManagerProvider {

    LineManager getPiplelineManager(MQContext context, PiplelineInfo pipleline);

    LineManager getlooplinemanager(MQContext context, LooplineInfo loopLine);

    void init();
}
