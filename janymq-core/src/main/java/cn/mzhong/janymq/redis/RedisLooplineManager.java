package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.Loopline;

import java.util.Collections;
import java.util.List;

/**
 * Key Such as JSimpleMQ:Loopline:LoopLineID:[wait|done|error:lock]
 */
public class RedisLooplineManager extends RedisLineManager {

    protected String ID;

    private static String key(String keyPrefix, Loopline loopLine) {
        RedisKeyGenerator generator = new RedisKeyGenerator(loopLine.getValue());
        generator.append(loopLine.getVersion());
        return keyPrefix + ":Loopline:" + generator.generate();
    }

    public RedisLooplineManager(MQContext context, RedisLineManagerProvider provider, Loopline loopLine) {
        super(key(provider.keyPrefix, loopLine), provider.getJedisPool(), context);
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
