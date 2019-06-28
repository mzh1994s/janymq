package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LooplineInfo;

import java.util.Collections;
import java.util.List;

public class ZookeeperLooplineManager extends ZookeeperLineManager {
    ZookeeperLooplineManager(MQContext context, LooplineInfo loopline, String connectString, String root) {
        super(context, loopline, connectString, root);
    }

    @Override
    public List<String> keys() {
        // loopline打乱key顺序
        List<String> keys = zkClient.getChildren(waitPath);
        Collections.shuffle(keys);
        return keys;
    }
}
