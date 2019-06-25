package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.Message;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperLineManager implements LineManager {

    ZooKeeper zooKeeper;
    protected String parentPath;

    ZookeeperLineManager(ZooKeeper zooKeeper){
        this.zooKeeper = zooKeeper;
    }

    @Override
    public String ID() {
        return null;
    }

    @Override
    public void push(Message message) {
//        zooKeeper.create();
    }

    @Override
    public Message poll() {
        return null;
    }

    @Override
    public void back(Message message) {

    }

    @Override
    public void done(Message message) {

    }

    @Override
    public void error(Message message) {

    }

    @Override
    public long length() {
        return 0;
    }
}
