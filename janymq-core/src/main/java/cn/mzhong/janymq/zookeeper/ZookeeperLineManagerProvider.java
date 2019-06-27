package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LineManagerProvider;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.line.PiplelineInfo;

public class ZookeeperLineManagerProvider implements LineManagerProvider {

    protected String connectString;
    protected String root;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    @Override
    public LineManager getPiplelineManager(MQContext context, PiplelineInfo pipleline) {
        return new ZookeeperPiplelineManager(context, pipleline, connectString, root);
    }

    @Override
    public LineManager getlooplinemanager(MQContext context, LooplineInfo loopLine) {
        return new ZookeeperLooplineManager(context, loopLine, connectString, root);
    }

    @Override
    public void init() {

    }
}
