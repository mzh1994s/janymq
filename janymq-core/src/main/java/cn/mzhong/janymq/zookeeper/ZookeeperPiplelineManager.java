package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.PiplelineInfo;

import java.util.Collections;
import java.util.LinkedList;

public class ZookeeperPiplelineManager extends ZookeeperLineManager {

    ZookeeperPiplelineManager(MQContext context, PiplelineInfo pipleline, String connectString, String root) {
        super(context, pipleline, connectString, root);
    }

    @Override
    public LinkedList<String> keys() {
        LinkedList<String> list = new LinkedList<String>();
        list.addAll(zkClient.getChildren(waitPath));
        Collections.sort(list);
        return list;
    }
}
