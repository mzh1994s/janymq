package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LooplineInfo;

public class ZookeeperLooplineManager extends ZookeeperLineManager {
    ZookeeperLooplineManager(MQContext context, LooplineInfo loopline, String connectString, String root) {
        super(context, loopline, connectString, root);
    }
}
