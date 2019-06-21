package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.Pipleline;

import java.util.List;

public class RedisPiplelineManager extends RedisLineManager {

    protected String ID;

    private static String key(String keyPrefix, Pipleline pipleline) {
        RedisKeyGenerator keyGenerator = new RedisKeyGenerator(pipleline.getValue());
        keyGenerator.append(pipleline.getVersion());
        return keyPrefix + ":Pipleline:" + keyGenerator.generate();
    }

    public RedisPiplelineManager(MQContext context, RedisLineManagerProvider provider, Pipleline pipleline) {
        super(key(provider.keyPrefix, pipleline), provider.getJedisPool(), context);
        this.ID = pipleline.ID();
    }

    @Override
    protected void keyFilter(List<byte[]> keys) {
        // 忽略;
    }

    @Override
    public String ID() {
        return this.ID;
    }
}
