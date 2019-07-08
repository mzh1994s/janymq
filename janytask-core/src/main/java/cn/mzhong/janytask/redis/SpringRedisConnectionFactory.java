package cn.mzhong.janytask.redis;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;

public class SpringRedisConnectionFactory implements RedisConnectionFactory {

    public SpringRedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    protected JedisConnectionFactory jedisConnectionFactory;

    @Override
    public Jedis getConnection() {
        return (Jedis) jedisConnectionFactory.getConnection().getNativeConnection();
    }
}
