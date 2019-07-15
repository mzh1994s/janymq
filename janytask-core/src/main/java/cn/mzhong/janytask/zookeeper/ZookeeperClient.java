package cn.mzhong.janytask.zookeeper;

import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperClient implements Watcher {

    protected ZooKeeper zookeeper;
    CountDownLatch initCountDownLatch = new CountDownLatch(1);

    public ZookeeperClient(String connectString) {
        try {
            this.zookeeper = new ZooKeeper(connectString, 1500, this);
            initCountDownLatch.await();
        } catch (Exception e) {
            throw new RuntimeException("Zookeeper客户端初始化失败！", e);
        }
    }

    /**
     * 创建节点
     *
     * @param path       必须使用标准的路径格式，如：/rootPath/node1/node2
     * @param data
     * @param createMode
     * @return
     */
    public boolean create(String path, byte[] data, CreateMode createMode) {
        try {
            this.zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
            return true;
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                return false;
            } else if (e.code() == KeeperException.Code.NONODE) {
                // 创建父级目录
                String parentPath = path.substring(0, path.lastIndexOf('/'));
                create(parentPath, null, CreateMode.PERSISTENT);
                return create(path, data, createMode);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String path) {
        try {
            zookeeper.delete(path, 0);
        } catch (Exception e) {
            throw new RuntimeException("删除Zookeeper路径出现错误！", e);
        }
    }

    public List<String> getChildren(String path) {
        try {
            return zookeeper.getChildren(path, false);
        } catch (Exception e) {
            throw new RuntimeException("获取Zookeeper子节点出错！", e);
        }
    }

    public byte[] getData(String path) {
        try {
            return zookeeper.getData(path, false, null);
        } catch (Exception e) {
            throw new RuntimeException("获取Zookeeper数据出错！", e);
        }
    }

    public void process(WatchedEvent watchedEvent) {
        initCountDownLatch.countDown();
    }
}
