package cn.mzhong.janymq.zookeeper;

import org.apache.zookeeper.ZooKeeper;

public class ZookeeperPiplelineManager extends ZookeeperLineManager {

    ZookeeperPiplelineManager(ZooKeeper zooKeeper) {
        super(zooKeeper);
    }
}
