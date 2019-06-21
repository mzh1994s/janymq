package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineIDGenerator;

import java.util.Collections;
import java.util.List;

/**
 * Key Such as JSimpleMQ:Loopline:LoopLineID:[wait|done|error:lock]
 */
public class RedisLooplineManager extends RedisLineManager {

    protected String lineId;

    private static String key(String keyPrefix, Loopline loopLine) {
        RedisKeyGenerator generator = new RedisKeyGenerator(loopLine.value());
        generator.append(loopLine.version());
        return keyPrefix + ":Loopline:" + generator.generate();
    }

    public RedisLooplineManager(MQContext context, RedisLineManagerProvider provider, Loopline loopLine) {
        super(key(provider.keyPrefix, loopLine), provider.getJedisPool(), context);
        lineId = LineIDGenerator.generate(loopLine);
    }

    @Override
    protected void keyFilter(List<byte[]> keys) {
        // 打乱顺序
        Collections.shuffle(keys);
    }

    @Override
    public String lineId() {
        return lineId;
    }
}
