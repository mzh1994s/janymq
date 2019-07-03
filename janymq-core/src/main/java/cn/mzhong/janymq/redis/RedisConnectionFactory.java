package cn.mzhong.janymq.redis;

import redis.clients.jedis.Jedis;

public interface RedisConnectionFactory {
    /**
     * 获取Jedis连接
     *
     * @return
     */
    Jedis getConnection();
}
