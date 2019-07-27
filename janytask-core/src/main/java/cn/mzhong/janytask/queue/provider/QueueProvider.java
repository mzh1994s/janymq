package cn.mzhong.janytask.queue.provider;

import cn.mzhong.janytask.core.TaskComponent;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;

/**
 * 流水线管理器生产者，实现{@link QueueProvider}可以支持任何中间件。<br/>
 * 一般情况下，一个新的中间件要对接JSimpleMQ只需要实现{@link QueueProvider}和{@link MessageDao}两个接口<br/>
 */
public interface QueueProvider extends TaskComponent {

    String[] getPackages();

    MessageDao createMessageDao(QueueInfo queueInfo);
}
