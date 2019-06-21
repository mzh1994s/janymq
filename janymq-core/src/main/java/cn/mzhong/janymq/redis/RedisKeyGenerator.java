package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.line.LineIDGenerator;

public class RedisKeyGenerator extends LineIDGenerator {

    public RedisKeyGenerator(String node) {
        super(node, ":");
    }
}
