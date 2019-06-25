package cn.mzhong.janymq.zookeeper;

import org.apache.zookeeper.ZooKeeper;

public class ZookeeperLooplineManager extends ZookeeperLineManager {
    ZookeeperLooplineManager(ZooKeeper zooKeeper) {
        super(zooKeeper);
    }
}
