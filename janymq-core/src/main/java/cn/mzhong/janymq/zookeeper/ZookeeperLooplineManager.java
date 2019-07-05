package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LooplineInfo;

import java.util.LinkedList;

public class ZookeeperLooplineManager extends ZookeeperLineManager {
    ZookeeperLooplineManager(MQContext context, LooplineInfo loopline, String connectString, String root) {
        super(context, loopline, connectString, root);
    }

    @Override
    public LinkedList<String> keys() {
        LinkedList<String> list = new LinkedList<String>();
        list.addAll(zkClient.getChildren(waitPath));
        return list;
    }

}
