package cn.mzhong.janytask.redis;

import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.tool.IDGenerator;

public class RedisKeyGenerator extends IDGenerator {

    public RedisKeyGenerator(QueueInfo lineInfo) {
        super(lineInfo.getValue(), ":");
        append(lineInfo.getVersion());
    }
}
