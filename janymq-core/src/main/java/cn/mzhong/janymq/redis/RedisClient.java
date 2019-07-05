package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.tool.PRInvoker;
import redis.clients.jedis.Jedis;

public class RedisClient {
    protected RedisConnectionFactory connectionFactory;

    public RedisClient(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public <T> T execute(PRInvoker<Jedis, T> invoker) {
        Jedis jedis = connectionFactory.getConnection();
        try {
            return invoker.invoke(jedis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            jedis.close();
        }
    }
}
