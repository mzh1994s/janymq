package cn.mzhong.janymq.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class GenericRedisConnectionFactory implements RedisConnectionFactory {

    JedisPool jedisPool;

    public GenericRedisConnectionFactory(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public Jedis getConnection() {
        return jedisPool.getResource();
    }
}
