package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LooplineInfo;

import java.util.Collections;
import java.util.List;

/**
 * Key Such as JSimpleMQ:LooplineInfo:LoopLineID:[wait|done|error:lock]
 */
public class RedisLooplineManager extends RedisLineManager {

    protected String ID;

    private static String key(String rootPath, LooplineInfo loopLine) {
        RedisKeyGenerator generator = new RedisKeyGenerator(loopLine);
        return rootPath + ":Loopline:" + generator.generate();
    }

    public RedisLooplineManager(MQContext context, RedisLineManagerProvider provider, LooplineInfo loopLine) {
        super(key(provider.rootPath, loopLine), provider.getConnectionFactory(), context);
        this.ID = loopLine.ID();
    }

    @Override
    protected void keyFilter(List<byte[]> keys) {
        // 打乱顺序
        Collections.shuffle(keys);
    }

    @Override
    public String ID() {
        return ID;
    }
}
