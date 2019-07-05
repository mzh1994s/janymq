package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineInfo;
import cn.mzhong.janymq.line.LockedLineManager;
import cn.mzhong.janymq.line.Message;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ZookeeperLineManager extends LockedLineManager {
    final static Logger Log = LoggerFactory.getLogger(ZookeeperLineManager.class);
    protected String connectString;
    protected ZookeeperClient zkClient;
    protected String waitPath;
    protected String donePath;
    protected String errorPath;
    protected String lockPath;
    protected String root;

    public void initZookeeperClient(String connectString) {
        this.connectString = connectString;
        this.zkClient = new ZookeeperClient(connectString);
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
        zkClient.create(waitPath, null, CreateMode.PERSISTENT);
        zkClient.create(donePath, null, CreateMode.PERSISTENT);
        zkClient.create(errorPath, null, CreateMode.PERSISTENT);
        zkClient.create(lockPath, null, CreateMode.PERSISTENT);
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

    protected void push(String key, Message message) {
        try {
            byte[] data = dataSerializer.serialize(message);
            String path = key + "/" + message.getKey();
            zkClient.create(path, data, CreateMode.PERSISTENT);
        } catch (Exception e) {
            throw new RuntimeException("推送消息出错！", e);
        }
    }

    protected void delete(String key, Message message) {
        zkClient.delete(key + "/" + message.getKey());
    }

    public void push(Message message) {
        push(waitPath, message);
    }

    public void done(Message message) {
        String key = message.getKey();
        push(donePath, message);
        delete(waitPath, message);
        unLock(key);
    }

    public void error(Message message) {
        String key = message.getKey();
        push(errorPath, message);
        delete(waitPath, message);
        unLock(key);
    }

    public long length() {
        return zkClient.getChildren(waitPath).size();
    }

    protected boolean lock(String key) {
        return zkClient.create(lockPath + "/" + key, null, CreateMode.EPHEMERAL);
    }

    protected boolean unLock(String key) {
        zkClient.delete(lockPath + "/" + key);
        return true;
    }

    @Override
    protected Message get(String key) {
        byte[] data = zkClient.getData(waitPath + "/" + key);
        try {
            return (Message) dataSerializer.deserialize(data);
        } catch (Exception e) {
            Log.error("消息反序列化出错，消息已被忽略！消息ID:" + key, e);
        }
        return null;
    }
}
