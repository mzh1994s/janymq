package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LineManagerProvider;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.line.PiplelineInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisLineManagerProvider implements LineManagerProvider {

    final static Logger Log = LoggerFactory.getLogger(RedisLineManagerProvider.class);

    // 根目录
    protected String rootPath = "janymq";
    // redis连接工厂
    protected RedisConnectionFactory connectionFactory;
    protected MQContext context;

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
    public void init(MQContext context) {
        this.context = context;
        if (this.connectionFactory == null) {
            throw new RuntimeException("无Redis连接工厂，请先指定Redis连接工厂！");
        }
        if (Log.isDebugEnabled()) {
            Log.debug(this.toString());
        }
    }

    @Override
    public LineManager getPiplelineManager(PiplelineInfo pipleline) {
        RedisPiplelineManager redisPiplelineManager = new RedisPiplelineManager(context, this, pipleline);
        return redisPiplelineManager;
    }

    @Override
    public LineManager getlooplinemanager(LooplineInfo loopLine) {
        RedisLooplineManager looplineManager = new RedisLooplineManager(context, this, loopLine);
        return looplineManager;
    }

    @Override
    public String toString() {
        return "RedisLineManagerProvider{" +
                "rootPath='" + rootPath + '\'' +
                '}';
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
