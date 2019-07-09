package cn.mzhong.janytask.zookeeper;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.QueueManager;
import cn.mzhong.janytask.queue.QueueProvider;
import cn.mzhong.janytask.queue.LooplineInfo;
import cn.mzhong.janytask.queue.PiplelineInfo;

public class ZookeeperLineManagerProvider implements QueueProvider {

    protected String connectString;
    protected String root = "janymq";
    protected TaskContext context;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    @Override
    public QueueManager getPiplelineManager(PiplelineInfo pipleline) {
        return new ZookeeperPiplelineManager(context, pipleline, connectString, root);
    }

    @Override
    public QueueManager getlooplinemanager(LooplineInfo loopLine) {
        return new ZookeeperLooplineManager(context, loopLine, connectString, root);
    }

    @Override
    public void init(TaskContext context) {
        this.context = context;
    }
}
