package cn.mzhong.janytask.queue.provider.redis;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;

public class SpringRedisConnectionFactory implements RedisConnectionFactory {

    public SpringRedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    protected JedisConnectionFactory jedisConnectionFactory;

    public Jedis getConnection() {
        return (Jedis) jedisConnectionFactory.getConnection().getNativeConnection();
    }
}
