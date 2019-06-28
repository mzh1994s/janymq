package cn.mzhong.janymq.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class ZookeeperClient implements Watcher {

    protected ZooKeeper zookeeper;

    public ZookeeperClient(String connectString) {
        try {
            this.zookeeper = new ZooKeeper(connectString, 1500, this);
        } catch (IOException e) {
            throw new RuntimeException("Zookeeper客户端初始化失败！", e);
        }
    }

    public boolean create(String path, byte[] data, CreateMode createMode) {
        // 得到标准的路径格式：/root/node1/node2
        if (path.charAt(0) != '/') {
            path = "/" + path;
        }
        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        int indexOfSeparator = path.lastIndexOf('/');
        System.out.println(path);
        try {
            // 如果不是root路径
            if (indexOfSeparator != 0) {
                // 创建上级路径
                String parentPath = path.substring(0, indexOfSeparator);
                Stat exists = this.zookeeper.exists(parentPath, false);
                if (exists == null) {
                    create(parentPath, null, createMode);
                }
            }
            // 如果已存在，则不创建
            Stat exists = this.zookeeper.exists(path, false);
            if (exists != null) {
                return false;
            }
            // 创建本级
            this.zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("创建Zookeeper路径出现错误！", e);
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

    @Override
    public void process(WatchedEvent watchedEvent) {
        // 无监听
    }
}
