package cn.mzhong.janytask.redis;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.PiplelineInfo;
import cn.mzhong.janytask.tool.PRInvoker;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class RedisPiplelineManager extends RedisLineManager {

    private static String key(String rootPath, PiplelineInfo pipleline) {
        RedisKeyGenerator keyGenerator = new RedisKeyGenerator(pipleline);
        return rootPath + ":Pipleline:" + keyGenerator.generate();
    }

    public RedisPiplelineManager(TaskContext context, RedisProvider provider, PiplelineInfo pipleline) {
        super(context, provider.getConnectionFactory(), pipleline, key(provider.rootPath, pipleline));
    }

    @Override
    protected LinkedList<String> idList() {
        return this.redisClient.execute(new PRInvoker<Jedis, LinkedList<String>>() {
            public LinkedList<String> invoke(Jedis jedis) throws Exception {
                LinkedList<String> list = new LinkedList<String>();
                Iterator<byte[]> iterator = jedis.hkeys(waitKey).iterator();
                while (iterator.hasNext()) {
                    list.add(new String(iterator.next()));
                }
                Collections.sort(list);
                return list;
            }
        });
    }
}
