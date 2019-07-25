package cn.mzhong.janytask.queue.provider.redis;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SpringRedisProvider extends SpringRedisProviderConfig implements QueueProvider {

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

    public MessageDao createMessageDao(QueueInfo queueInfo) {
        return new RedisMessageDao(context, this.connectionFactory, queueInfo, rootPath);
    }

    public void init() {
        if (jedisConnectionFactory == null) {
            if (jedisPool == null) {
                if (jedisPoolConfig == null) {
                    jedisPoolConfig = new JedisPoolConfig();
                }
                jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
            }
            connectionFactory = new GenericRedisConnectionFactory(jedisPool);
        } else {
            connectionFactory = new SpringRedisConnectionFactory(jedisConnectionFactory);
        }
        if (Log.isDebugEnabled()) {
            Log.debug(this.toString());
        }
    }
}
