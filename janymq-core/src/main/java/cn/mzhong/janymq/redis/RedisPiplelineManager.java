package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineIDGenerator;

import java.util.List;

public class RedisPiplelineManager extends RedisLineManager {

    protected String lineId;

    private static String key(String keyPrefix, Pipleline pipleline) {
        RedisKeyGenerator keyGenerator = new RedisKeyGenerator(pipleline.value());
        keyGenerator.append(pipleline.version());
        return keyPrefix + ":Pipleline:" + keyGenerator.generate();
    }

    public RedisPiplelineManager(MQContext context, RedisLineManagerProvider provider, Pipleline pipleline) {
        super(key(provider.keyPrefix, pipleline), provider.getJedisPool(), context);
        this.lineId = LineIDGenerator.generate(pipleline);
    }

    @Override
    protected void keyFilter(List<byte[]> keys) {
        // 忽略;
    }

    @Override
    public String lineId() {
        return this.lineId;
    }
}
