package cn.mzhong.janymq.zookeeper;

import cn.mzhong.janymq.line.LineIDGenerator;

public class ZookeeperPathGenerator extends LineIDGenerator {

    public ZookeeperPathGenerator(String root) {
        super(root, "/");
    }
}
