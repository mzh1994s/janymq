package cn.mzhong.janytask.provider.zookeeper;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.*;

public class ZookeeperProvider implements QueueProvider {

    protected String connectString;
    protected String root = "janytask";
    protected TaskContext context;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public MessageDao createMessageDao(QueueInfo queueInfo) {
        return new ZookeeperMessageDao(context, queueInfo, connectString, root);
    }

    public void init(TaskContext context) {
        this.context = context;
    }
}
