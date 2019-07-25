package cn.mzhong.janytask.queue.provider.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

public class RedisProviderConfig implements Serializable {
    protected String rootPath = "janytask";
    protected String host;
    protected int port;
    protected int timeout;
    protected String password;
    protected int database;
    // refference
    protected JedisPoolConfig jedisPoolConfig;
    protected JedisPool jedisPool;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        this.jedisPoolConfig = jedisPoolConfig;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public String toString() {
        return "RedisProviderConfig{" +
                "rootPath='" + rootPath + '\'' +
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
