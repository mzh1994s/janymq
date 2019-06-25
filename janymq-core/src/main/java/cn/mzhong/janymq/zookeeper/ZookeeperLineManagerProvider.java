package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LineManagerProvider;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.line.PiplelineInfo;

public class ZookeeperLineManagerProvider implements LineManagerProvider {

    @Override
    public LineManager getPiplelineManager(MQContext context, PiplelineInfo pipleline) {
        return null;
    }

    @Override
    public LineManager getlooplinemanager(MQContext context, LooplineInfo loopLine) {
        return null;
    }

    @Override
    public void init() {

    }
}
