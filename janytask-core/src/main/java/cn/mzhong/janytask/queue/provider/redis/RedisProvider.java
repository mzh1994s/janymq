package cn.mzhong.janytask.queue.provider.redis;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisProvider extends AbstractRedisProvider {

    final static Logger Log = LoggerFactory.getLogger(RedisProvider.class);

    // redis连接工厂
    RedisConnectionFactory connectionFactory;
    protected TaskContext context;

    public void setContext(TaskContext context) {
        this.context = context;
    }

    public RedisConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void init() {
        if (jedisPool == null) {
            if (jedisPoolConfig == null) {
                jedisPoolConfig = new JedisPoolConfig();
            }
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
        }
        connectionFactory = new GenericRedisConnectionFactory(jedisPool);
        if (Log.isDebugEnabled()) {
            Log.debug(this.toString());
        }
    }

    public MessageDao createMessageDao(QueueInfo queueInfo) {
        return new RedisMessageDao(context, this.connectionFactory, queueInfo, rootPath);
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
        provider.setJedisPool(jedisPool);
        return provider;
    }
}
