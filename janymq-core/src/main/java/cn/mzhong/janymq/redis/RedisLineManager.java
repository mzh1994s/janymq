package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineInfo;
import cn.mzhong.janymq.line.LockedLineManager;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.util.PRInvoker;
import redis.clients.jedis.Jedis;

public abstract class RedisLineManager extends LockedLineManager {
    protected byte[] waitKey;
    protected byte[] doneKey;
    protected byte[] errorKey;
    protected byte[] lockKey;
    protected RedisClient redisClient;

    public RedisLineManager(MQContext context, RedisConnectionFactory connectionFactory, LineInfo lineInfo, String keyPrefix) {
        super(context, lineInfo);
        this.redisClient = new RedisClient(connectionFactory);
        this.waitKey = (keyPrefix + ":wait").getBytes();
        this.doneKey = (keyPrefix + ":done").getBytes();
        this.errorKey = (keyPrefix + ":error").getBytes();
        this.lockKey = (keyPrefix + ":lock").getBytes();
    }

    public void push(final Message message) {
        this.redisClient.execute(new PRInvoker<Jedis, Long>() {
            public Long invoke(Jedis jedis) throws Exception {
                byte[] data = dataSerializer.serialize(message);
                return jedis.hset(waitKey, message.getKey().getBytes(), data);
            }
        });
    }

    @Override
    protected Message get(final String key) {
        return this.redisClient.execute(new PRInvoker<Jedis, Message>() {
            public Message invoke(Jedis jedis) throws Exception {
                byte[] messageByes = jedis.hget(waitKey, key.getBytes());
                return (Message) dataSerializer.deserialize(messageByes);
            }
        });
    }

    private void complete(final byte[] key, final Message message) {
        this.redisClient.execute(new PRInvoker<Jedis, Boolean>() {
            public Boolean invoke(Jedis jedis) throws Exception {
                byte[] data = dataSerializer.serialize(message);
                String field = message.getKey();
                byte[] fieldBytes = message.getKey().getBytes();
                // 转入新表
                jedis.hset(key, fieldBytes, data);
                // 删除旧表数据
                jedis.hdel(waitKey, fieldBytes);
                // 将锁删除
                return unLock(field);
            }
        });
    }

    public void done(Message message) {
        complete(doneKey, message);
    }

    public void error(Message message) {
        complete(errorKey, message);
    }

    public long length() {
        return this.redisClient.execute(new PRInvoker<Jedis, Long>() {
            public Long invoke(Jedis jedis) throws Exception {
                return jedis.hlen(waitKey);
            }
        });
    }

    @Override
    protected boolean lock(final String key) {
        return redisClient.execute(new PRInvoker<Jedis, Boolean>() {
            public Boolean invoke(Jedis jedis) throws Exception {
                byte[] value = (System.currentTimeMillis() + "").getBytes();
                return jedis.hsetnx(lockKey, key.getBytes(), value) == 1;
            }
        });
    }

    @Override
    protected boolean unLock(final String key) {
        return redisClient.execute(new PRInvoker<Jedis, Boolean>() {
            public Boolean invoke(Jedis jedis) throws Exception {
                byte[] value = (System.currentTimeMillis() + "").getBytes();
                return jedis.hdel(lockKey, key.getBytes()) == 1;
            }
        });
    }
}
