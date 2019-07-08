package cn.mzhong.janytask.zookeeper;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.LooplineInfo;

import java.util.LinkedList;

public class ZookeeperLooplineManager extends ZookeeperLineManager {
    ZookeeperLooplineManager(TaskContext context, LooplineInfo loopline, String connectString, String root) {
        super(context, loopline, connectString, root);
    }

    @Override
    public LinkedList<String> idList() {
        LinkedList<String> list = new LinkedList<String>();
        list.addAll(zkClient.getChildren(waitPath));
        return list;
    }

}
