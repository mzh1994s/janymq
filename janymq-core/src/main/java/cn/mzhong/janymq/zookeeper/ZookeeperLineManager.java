package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.AbstractLineManager;
import cn.mzhong.janymq.line.LineInfo;
import cn.mzhong.janymq.line.Message;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public abstract class ZookeeperLineManager extends AbstractLineManager {

    protected String connectString;
    protected ZooKeeper zooKeeper;
    protected String parentPath;

    public static ZooKeeper createZookeeper(String connectString){
        try {
            return new ZooKeeper(connectString, 1500, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    ;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Zookeeper客户端初始化失败！", e);
        }
    }

    ZookeeperLineManager(MQContext context, LineInfo lineInfo, String connectString) {
        super(context, lineInfo);
        this.connectString = connectString;
        this.zooKeeper = createZookeeper(connectString);

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
