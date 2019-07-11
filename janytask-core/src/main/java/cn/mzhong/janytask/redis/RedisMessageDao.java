package cn.mzhong.janytask.redis;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.LockedQueueManager;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.tool.PRInvoker;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.LinkedList;

public class RedisMessageDao extends LockedQueueManager {
    protected byte[] waitKey;
    protected byte[] doneKey;
    protected byte[] errorKey;
    protected byte[] lockKey;
    protected RedisClient redisClient;

    private static String key(String rootPath, QueueInfo queueInfo) {
        RedisKeyGenerator keyGenerator = new RedisKeyGenerator(queueInfo);
        return rootPath + ":queue:" + keyGenerator.generate();
    }

    public RedisMessageDao(TaskContext context, RedisConnectionFactory connectionFactory, QueueInfo queueInfo, String rootPath) {
        super(context, queueInfo);
        String keyPrefix = key(rootPath, queueInfo);
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
                return jedis.hset(waitKey, message.getId().getBytes(), data);
            }
        });
    }

    @Override
    protected Message get(final String id) {
        return this.redisClient.execute(new PRInvoker<Jedis, Message>() {
            public Message invoke(Jedis jedis) throws Exception {
                byte[] messageByes = jedis.hget(waitKey, id.getBytes());
                return (Message) dataSerializer.deserialize(messageByes);
            }
        });
    }

    private void complete(final byte[] key, final Message message) {
        this.redisClient.execute(new PRInvoker<Jedis, Boolean>() {
            public Boolean invoke(Jedis jedis) throws Exception {
                byte[] data = dataSerializer.serialize(message);
                String field = message.getId();
                byte[] fieldBytes = message.getId().getBytes();
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
    protected boolean lock(final String id) {
        return redisClient.execute(new PRInvoker<Jedis, Boolean>() {
            public Boolean invoke(Jedis jedis) throws Exception {
                byte[] value = (System.currentTimeMillis() + "").getBytes();
                return jedis.hsetnx(lockKey, id.getBytes(), value) == 1;
            }
        });
    }

    @Override
    protected boolean unLock(final String id) {
        return redisClient.execute(new PRInvoker<Jedis, Boolean>() {
            public Boolean invoke(Jedis jedis) throws Exception {
                byte[] value = (System.currentTimeMillis() + "").getBytes();
                return jedis.hdel(lockKey, id.getBytes()) == 1;
            }
        });
    }

    protected LinkedList<String> idList() {
        return this.redisClient.execute(new PRInvoker<Jedis, LinkedList<String>>() {
            public LinkedList<String> invoke(Jedis jedis) throws Exception {
                LinkedList<String> list = new LinkedList<String>();
                Iterator<byte[]> iterator = jedis.hkeys(waitKey).iterator();
                while (iterator.hasNext()) {
                    list.add(new String(iterator.next()));
                }
                return list;
            }
        });
    }
}
