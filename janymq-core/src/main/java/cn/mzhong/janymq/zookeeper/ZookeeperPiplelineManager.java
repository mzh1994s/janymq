package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.PiplelineInfo;

public class ZookeeperPiplelineManager extends ZookeeperLineManager {

    ZookeeperPiplelineManager(MQContext context, PiplelineInfo pipleline, String connectString, String root) {
        super(context, pipleline, connectString, root);
    }
}
