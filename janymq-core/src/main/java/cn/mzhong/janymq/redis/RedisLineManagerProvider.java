package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.line.PiplelineInfo;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LineManagerProvider;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisLineManagerProvider extends JedisPoolConfig implements LineManagerProvider {

    protected String hostName = "localhost";
    protected int port = 6379;
    protected String password;
    protected int database = 0;
    protected int timeout = 0;
    protected String keyPrefix = "JAnyMQ";
    // jedis连接池实例
    protected JedisPool jedisPool;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }


    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void init() {
        jedisPool = new JedisPool(this, hostName, port, timeout, password, database);
    }

    @Override
    public LineManager getPiplelineManager(MQContext context, PiplelineInfo pipleline) {
        RedisPiplelineManager redisPiplelineManager = new RedisPiplelineManager(context, this, pipleline);
        return redisPiplelineManager;
    }

    @Override
    public LineManager getlooplinemanager(MQContext context, LooplineInfo loopLine) {
        RedisLooplineManager looplineManager = new RedisLooplineManager(context, this, loopLine);
        return looplineManager;
    }
}
