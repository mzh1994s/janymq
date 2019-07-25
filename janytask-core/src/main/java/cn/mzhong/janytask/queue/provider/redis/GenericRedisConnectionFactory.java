package cn.mzhong.janytask.queue.provider.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class GenericRedisConnectionFactory implements RedisConnectionFactory {

    JedisPool jedisPool;

    public GenericRedisConnectionFactory(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Jedis getConnection() {
        return jedisPool.getResource();
    }
}
