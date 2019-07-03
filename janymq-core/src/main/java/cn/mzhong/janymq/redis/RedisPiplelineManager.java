package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.PiplelineInfo;

import java.util.List;

public class RedisPiplelineManager extends RedisLineManager {

    protected String ID;

    private static String key(String rootPath, PiplelineInfo pipleline) {
        RedisKeyGenerator keyGenerator = new RedisKeyGenerator(pipleline);
        return rootPath + ":Pipleline:" + keyGenerator.generate();
    }

    public RedisPiplelineManager(MQContext context, RedisLineManagerProvider provider, PiplelineInfo pipleline) {
        super(key(provider.rootPath, pipleline), provider.getConnectionFactory(), context);
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
