package cn.mzhong.janytask.zookeeper;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.PiplelineInfo;

import java.util.Collections;
import java.util.LinkedList;

public class ZookeeperPiplelineManager extends ZookeeperLineManager {

    ZookeeperPiplelineManager(TaskContext context, PiplelineInfo pipleline, String connectString, String root) {
        super(context, pipleline, connectString, root);
    }

    @Override
    public LinkedList<String> idList() {
        LinkedList<String> list = new LinkedList<String>();
        list.addAll(zkClient.getChildren(waitPath));
        Collections.sort(list);
        return list;
    }
}
