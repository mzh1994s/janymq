package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.PiplelineInfo;

import java.util.Collections;
import java.util.List;

public class ZookeeperPiplelineManager extends ZookeeperLineManager {

    ZookeeperPiplelineManager(MQContext context, PiplelineInfo pipleline, String connectString, String root) {
        super(context, pipleline, connectString, root);
    }

    @Override
    public List<String> keys() {
        // loopline打乱key顺序
        List<String> keys = zkClient.getChildren(waitPath);
        Collections.sort(keys);
        return keys;
    }
}
