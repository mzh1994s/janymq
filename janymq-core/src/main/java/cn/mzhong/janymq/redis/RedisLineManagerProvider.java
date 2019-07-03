package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LineManagerProvider;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.line.PiplelineInfo;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisLineManagerProvider implements LineManagerProvider {

    // 根目录
    protected String rootPath = "janymq";
    // redis连接工厂
    protected RedisConnectionFactory connectionFactory;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public RedisConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void init() {
        if (this.connectionFactory == null) {
            throw new RuntimeException("无Redis连接工厂，请先指定Redis连接工厂！");
        }
    }

    @Override
    public LineManager getPiplelineManager(MQContext context, PiplelineInfo pipleline) {
        RedisPiplelineManager redisPiplelineManager = new RedisPiplelineManager(context, this, pipleline);
        return redisPiplelineManager;
    }

    @Override
    public LineManager getlooplinemanager(MQContext context, LooplineInfo loopLine) {
        RedisLooplineManager looplineManager = new RedisLooplineManager(context, this, loopLine);
        return looplineManager;
    }

    public static RedisLineManagerProvider create(
            String hostName,
            int port) {
        return create(new JedisPoolConfig(), hostName, port, null, 0, 0);
    }

    public static RedisLineManagerProvider create(
            String hostName,
            int port,
            String password,
            int database,
            int timeout) {
        return create(new JedisPoolConfig(), hostName, port, password, database, timeout);
    }

    public static RedisLineManagerProvider create(
            JedisPoolConfig config,
            String hostName,
            int port,
            String password,
            int database,
            int timeout) {
        JedisPool jedisPool = new JedisPool(config, hostName, port, timeout, password, database);
        return create(jedisPool);
    }

    public static RedisLineManagerProvider create(JedisPool jedisPool) {
        RedisLineManagerProvider provider = new RedisLineManagerProvider();
        RedisConnectionFactory connectionFactory = new GenericRedisConnectionFactory(jedisPool);
        provider.setConnectionFactory(connectionFactory);
        return provider;
    }
}
