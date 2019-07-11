package cn.mzhong.janytask.redis;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisProvider implements QueueProvider {

    final static Logger Log = LoggerFactory.getLogger(RedisProvider.class);

    // 根目录
    protected String rootPath = "janytask";
    // redis连接工厂
    protected RedisConnectionFactory connectionFactory;
    protected TaskContext context;

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

    public void init(TaskContext context) {
        this.context = context;
        if (this.connectionFactory == null) {
            throw new RuntimeException("无Redis连接工厂，请先指定Redis连接工厂！");
        }
        if (Log.isDebugEnabled()) {
            Log.debug(this.toString());
        }
    }

    public MessageDao createMessageDao(QueueInfo queueInfo) {
        return new RedisMessageDao(context, this.connectionFactory, queueInfo, rootPath);
    }

    @Override
    public String toString() {
        return "RedisProvider{" +
                "rootPath='" + rootPath + '\'' +
                '}';
    }

    public static RedisProvider create(
            String hostName,
            int port) {
        return create(new JedisPoolConfig(), hostName, port, null, 0, 0);
    }

    public static RedisProvider create(
            String hostName,
            int port,
            String password,
            int database,
            int timeout) {
        return create(new JedisPoolConfig(), hostName, port, password, database, timeout);
    }

    public static RedisProvider create(
            JedisPoolConfig config,
            String hostName,
            int port,
            String password,
            int database,
            int timeout) {
        JedisPool jedisPool = new JedisPool(config, hostName, port, timeout, password, database);
        return create(jedisPool);
    }

    public static RedisProvider create(JedisPool jedisPool) {
        RedisProvider provider = new RedisProvider();
        RedisConnectionFactory connectionFactory = new GenericRedisConnectionFactory(jedisPool);
        provider.setConnectionFactory(connectionFactory);
        return provider;
    }
}
