package cn.mzhong.janytask.queue.provider.redis;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

public class SpringRedisProviderConfig extends AbstractRedisProvider {

    protected JedisConnectionFactory jedisConnectionFactory;

    public JedisConnectionFactory getJedisConnectionFactory() {
        return jedisConnectionFactory;
    }

    public void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    @Override
    public String toString() {
        return "SpringRedisProviderConfig{" +
                "jedisConnectionFactory=" + jedisConnectionFactory +
                ", rootPath='" + rootPath + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", timeout=" + timeout +
                ", password='" + password + '\'' +
                ", database=" + database +
                ", jedisPoolConfig=" + jedisPoolConfig +
                ", jedisPool=" + jedisPool +
                '}';
    }
}
