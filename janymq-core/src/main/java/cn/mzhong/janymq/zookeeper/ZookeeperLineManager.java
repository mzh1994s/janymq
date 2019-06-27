package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.AbstractLineManager;
import cn.mzhong.janymq.line.LineInfo;
import cn.mzhong.janymq.line.Message;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ZookeeperLineManager extends AbstractLineManager implements Watcher {

    protected String connectString;
    protected ZookeeperClient zkclient;
    protected String waitPath;
    protected String donePath;
    protected String errorPath;
    protected String lockPath;
    protected String root;
    protected List<String> cacheKeys = new ArrayList<>();

    public void initZookeeperClient(String connectString) {
        this.connectString = connectString;
        try {
            this.zkclient = new ZookeeperClient(new ZooKeeper(connectString, 1500, this));
        } catch (IOException e) {
            throw new RuntimeException("Zookeeper客户端初始化失败！", e);
        }
    }

    public void initParentPath() {
        ZookeeperPathGenerator pathGenerator = new ZookeeperPathGenerator(this.root);
        pathGenerator.append(lineInfo.getValue()).append(lineInfo.getVersion());
        String parentPath = pathGenerator.generate();
        this.waitPath = parentPath + "/wait";
        this.donePath = parentPath + "/done";
        this.errorPath = parentPath + "/error";
        this.lockPath = parentPath + "/lock";
        // 创建父级目录
        zkclient.create(waitPath, null, CreateMode.PERSISTENT);
        zkclient.create(donePath, null, CreateMode.PERSISTENT);
        zkclient.create(errorPath, null, CreateMode.PERSISTENT);
        zkclient.create(lockPath, null, CreateMode.PERSISTENT);
    }

    public void initRootPath(String root) {
        this.root = root.startsWith("/") ? root : "/" + root;
    }

    ZookeeperLineManager(MQContext context, LineInfo lineInfo, String connectString, String root) {
        super(context, lineInfo);
        this.initZookeeperClient(connectString);
        this.initRootPath(root);
        this.initParentPath();
    }

    @Override
    public void push(Message message) {
        try {
            byte[] data = context.getDataSerializer().serialize(message);
            String path = waitPath + "/" + message.getKey();
            zkclient.create(path, data, CreateMode.PERSISTENT);
        } catch (Exception e) {
            throw new RuntimeException("推送消息出错！", e);
        }
    }

    @Override
    public Message poll() {
        if(cacheKeys.isEmpty()){
            cacheKeys = zkclient.getChildren(waitPath);
        }
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
        return zkclient.getChildren(waitPath).size();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        // 无监听逻辑
    }
}
