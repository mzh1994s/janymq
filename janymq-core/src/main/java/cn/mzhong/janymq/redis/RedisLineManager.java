package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.DataSerializer;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.LinkedList;
import java.util.List;

public abstract class RedisLineManager implements LineManager {
    final static Logger Log = LoggerFactory.getLogger(RedisLooplineManager.class);

    protected byte[] waitKey;
    protected byte[] doneKey;
    protected byte[] errorKey;
    protected byte[] lockKey;
    protected JedisPool jedisPool;
    protected DataSerializer serializer;
    protected LinkedList<byte[]> cacheKeys = new LinkedList<>();
    protected MQContext context;

    public RedisLineManager(String keyPrefixKey, JedisPool jedisPool, MQContext context) {
        this.waitKey = (keyPrefixKey + ":wait").getBytes();
        this.doneKey = (keyPrefixKey + ":done").getBytes();
        this.errorKey = (keyPrefixKey + ":error").getBytes();
        this.lockKey = (keyPrefixKey + ":lock").getBytes();
        this.jedisPool = jedisPool;
        this.serializer = context.getDataSerializer();
        this.context = context;
    }

    /**
     * 数据加锁
     *
     * @param key
     * @return
     */
    protected boolean lock(Jedis jedis, byte[] key) {
        boolean locked = false;
        try {
            byte[] value = (System.currentTimeMillis() + "").getBytes();
            locked = jedis.hsetnx(lockKey, key, value) == 1;
        } catch (Exception e) {
            Log.error("索引'" + new String(key) + "'加锁异常", e);
        } finally {
            jedis.close();
        }
        return locked;
    }

    /**
     * 数据解锁
     *
     * @param key
     * @return
     */
    protected boolean unlock(Jedis jedis, byte[] key) {
        boolean unlocked = false;
        try {
            unlocked = jedis.hdel(lockKey, key) == 1;
        } catch (Exception e) {
            Log.error("索引'" + new String(key) + "'解锁异常", e);
        } finally {
            jedis.close();
        }
        return unlocked;
    }

    protected abstract void keyFilter(List<byte[]> keys);

    @Override
    public Message poll() {
        Message message = null;
        Jedis jedis = jedisPool.getResource();
        try {
            if (cacheKeys.isEmpty()) {
                cacheKeys.addAll(jedis.hkeys(waitKey));
                keyFilter(cacheKeys);
                System.out.println("全量key" + cacheKeys.size());
            }
            // 可能当前缓存的key中某些已经被处理了。
            // 所以遍历列表，查找能被处理的key
            while (!cacheKeys.isEmpty()) {
                // ShutdownBreak;
                if (context.isShutdown()) {
                    break;
                }
                byte[] key = cacheKeys.poll();
                // 如果key能被加锁，证明可被处理
                if (key != null && lock(jedis, key)) {
                    byte[] data = jedis.hget(waitKey, key);
                    if (data != null) {
                        message = (Message) serializer.deserialize(data);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.error("拉取消息异常", e);
        } finally {
            jedis.close();
        }
        return message;
    }

    @Override
    public void push(Message message) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] data = serializer.serialize(message);
            jedis.hset(waitKey, message.getKey().getBytes(), data);
        } catch (Exception e) {
            Log.error("推送消息异常", e);
        } finally {
            jedis.close();
        }
    }

    private void complete(byte[] key, Message message) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] data = serializer.serialize(message);
            byte[] field = message.getKey().getBytes();
            // 转入新表
            jedis.hset(key, field, data);
            // 删除旧表数据
            jedis.hdel(waitKey, field);
            // 将锁删除
            unlock(jedis, field);
        } catch (Exception e) {
            Log.error("推送消息异常", e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public void done(Message message) {
        complete(doneKey, message);
    }

    @Override
    public void error(Message message) {
        complete(errorKey, message);
    }

    @Override
    public void back(Message message) {
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] field = message.getKey().getBytes();
            // 将锁删除
            unlock(jedis, field);
        } catch (Exception e) {
            Log.error("归还消息异常", e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public long length() {
        long len = 0;
        Jedis jedis = jedisPool.getResource();
        try {
            len = jedis.hlen(waitKey);
        } catch (Exception e) {
            Log.error("归还消息异常", e);
        } finally {
            jedis.close();
        }
        return len;
    }
}
