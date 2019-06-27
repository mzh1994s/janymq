package cn.mzhong.janymq.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class ZookeeperClient {

    protected ZooKeeper zookeeper;

    public ZookeeperClient(ZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
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
            if(exists != null){
                return false;
            }
            // 创建本级
            this.zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Zookeeper创建路径出现错误！", e);
        }
    }

    public List<String> getChildren(String path) {
        try {
            return zookeeper.getChildren(path, false);
        } catch (Exception e) {
            throw new RuntimeException("获取子节点出错！", e);
        }
    }
}
